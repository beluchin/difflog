(ns difflog.domain)

(declare line-delimiter word-diffs contains-diff? ignore-rules)
(defn difflog
  "l, r are text"
  ([l r] (difflog l r {}))
  ([l r ignore-rule-specs]
   (let [llines (.split l line-delimiter)
         rlines (.split r line-delimiter)
         rules (ignore-rules ignore-rule-specs)]
     (remove (comp not contains-diff?)
             (map #(word-diffs %1 %2 rules) llines rlines)))))

(declare to-num-or-na)
(def ^:const line-delimiter (System/lineSeparator))
(def ^:const word-delimiter "\\s+")
(def trans-pred
  ^{:doc (str "transformer: [l r] -> [newl newr] | :na (not-applicable)"
              "predicate: x -> l r idx -> true | false")
    :const true}
  {:col {:transformer identity
         :predicate-builder (fn [x] (if (set? x)
                              #(contains? x %3)
                              #(= x %3)))}
   :num {:transformer to-num-or-na
         :predicate-builder identity}
   :word {:transformer identity
          :predicate-builder (fn [k v] (fn [l r _] (and (= k l) (= v r))))}})

(defn- ignore-string-rule [k v]
  (let [trans-pred (trans-pred :word)
        transformer (:transformer trans-pred)
        predicate (:predicate-builder trans-pred)]
    [transformer (predicate k v)]))

(defn- ignore-rule [[k v]]
  (if (string? k)
    (ignore-string-rule k v)
    (let [trans-pred (trans-pred k)
          transformer (:transformer trans-pred)
          predicate-builder (:predicate-builder trans-pred)]
      [transformer (predicate-builder v)])))

(defn- ignore-rules [specs]
  (into {} (map ignore-rule specs)))

(defn- to-num-or-na [xs]
  (try
    (vec (map #(Double/valueOf %) xs))
    (catch NumberFormatException _
      :na)))

(defn- ignore-diff-one-rule? [l r idx [transformer predicate]]
  (let [transformed (transformer [l r])]
    (if (= :na transformed)
      false
      (apply #(predicate %1 %2 idx) transformed))))

(defn- ignore-diff? [l r idx rules]
  (some true? (map (partial ignore-diff-one-rule? l r idx) rules)))

(defn- diff-or-word
  "takes in two words. applies rules only if the words are different"
  [l r idx rules]
  (if (or (= l r) (ignore-diff? l r idx rules))
    l
    [l r]))

(defn- word-diffs
  "computes word difference between two lines
  ['hello world' 'goodbye world' {}] => [[hello goodbye] world]
  ['a' 'b' {'a' 'b'}] => (empty? ...)"
  [l r rules]
  (let [lwords (.split l word-delimiter)
        rwords (.split r word-delimiter)
        range-from-1 (drop 1 (range))]
    (map #(diff-or-word %1 %2 %3 rules) lwords rwords range-from-1)))

(defn- contains-diff? [line]
  (some vector? line))

(defn- conj-if-diff [coll line]
  (if (contains-diff? line)
    (conj coll line)
    coll))


(comment
  (#{:a} :a)
  (#{:a} :b)
  (contains? #{:a} :a)
  (contains? #{:a} :b)
  
  (into {} [[:a 1] [:b 2]])
  (seq? [])
  (seq? {})
  (sequential? #{:a})
  (sequential? [])
  (sequential? '())
  (set? #{:a})
  ({:a 1} :b 0)
  
  (to-num-or-error "hello" "world")

  (Double/valueOf "123hello")
  
  (#(identity % %&) 1 2)
  (map identity {:a 1}) ; ([:a 1])
  (some true? [false])
  
  (seq {:a 1, :b 2})
  (seq (.split "        hello  \t  world              " "\\"))
  (seq (.split " hello " "\\s+"))
  (seq (.split "hello=123 mundo[456]" "[\\s=\\[\\]]+"))
   
  (map + [1 2] [100 200])

  (difflog "hello world" "goodbye world")
  (conj [1 2] 3)
  (vector? [])
  (empty? (seq))
)
