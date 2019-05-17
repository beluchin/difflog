(ns difflog.domain)

(declare line-delimiter word-diffs contains-diff? trans-preds)
(defn difflog
  "l, r are text"
  ([l r] (difflog l r {}))
  ([l r rules]
   (let [llines (.split l line-delimiter)
         rlines (.split r line-delimiter)
         t-ps (trans-preds rules)]
     (remove (comp not contains-diff?)
             (map #(word-diffs %1 %2 t-ps) llines rlines)))))

(def ^:const line-delimiter (System/lineSeparator))
(def ^:const token-delimiter "[\\s\\[\\]]")
(def ^:const split-pattern (re-pattern (format "(?<=\\S)(?=%s)|(?<=%s)(?=\\S)"
                                               token-delimiter
                                               token-delimiter)))

(defn- to-num-or-na [xs]
  (try
    (vec (map #(Double/valueOf %) xs))
    (catch NumberFormatException _
      :na)))

(def ^{:doc (clojure.string/join "\n"
             ["transformer: [l r] -> [newl newr] | :na (not-applicable)"
              "  predicate-builder: x -> l r idx -> true | false"])
       :const true
       :private true}
  rule-def
  {:col {:transformer identity
         :predicate-builder (fn [x] (if (set? x)
                                      #(contains? x %3)
                                      #(= x %3)))}
   :num {:transformer to-num-or-na
         :predicate-builder identity}
   :word {:transformer identity
          :predicate-builder (fn [k v] (fn [l r _] (and (= k l) (= v r))))}})

(defn- string-trans-pred [k v]
  (let [rd (rule-def :word)
        transformer (:transformer rd)
        predicate (:predicate-builder rd)]
    [transformer (predicate k v)]))

(defn- trans-pred [[k v :as rule]]
  (if (string? k)
    (string-trans-pred k v)
    (let [trans-pred (rule-def k)
          transformer (:transformer trans-pred)
          predicate-builder (:predicate-builder trans-pred)]
      [transformer (predicate-builder v)])))

(defn- trans-preds [rules]
  (set (map trans-pred rules)))

(defn- ignore-diff-one? [l r idx [transformer predicate]]
  (let [transformed (transformer [l r])]
    (if (= :na transformed)
      false
      (apply #(predicate %1 %2 idx) transformed))))

(defn- ignore-diff? [l r idx trans-preds]
  (some true? (map (partial ignore-diff-one? l r idx) trans-preds)))

(defn- diff-or-word
  "takes in two words. applies rules only if the words are different"
  [l r idx trans-preds]
  (if (or (= l r) (ignore-diff? l r idx trans-preds))
    l
    [l r]))

(defn- word-diffs
  "computes word difference between two lines
  ['hello world' 'goodbye world' {}] => [[hello goodbye] world]
  ['a' 'b' {'a' 'b'}] => (empty? ...)"
  [l r trans-preds]
  (let [lwords (.split split-pattern l)
        rwords (.split split-pattern r)
        range-from-1 (drop 1 (range))]
    (map #(diff-or-word %1 %2 %3 trans-preds) lwords rwords range-from-1)))

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
