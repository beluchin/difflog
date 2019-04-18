(ns difflog.domain)

(declare line-delimiter word-diffs contains-diff?)
(defn difflog
  "l, r are text"
  ([l r] (difflog l r {}))
  ([l r rules]
   (let [larr (.split l line-delimiter)
         rarr (.split r line-delimiter)]
     (remove (comp not contains-diff?) (map #(word-diffs %1 %2 rules) larr rarr)))))

(def ^:const line-delimiter (System/lineSeparator))
(def ^:const word-delimiter "\\s+")

(defn- to-num-or-error [& args]
  (try
    (vec (map #(Double/valueOf %) args))
    (catch NumberFormatException _
      :error)))

(def ^:const transformer {:num to-num-or-error})

(defn- ignore-diff-one-rule? [l r [k v]]
  (if (string? k)
    (and (= l k) (= r v)) ; word rule
    (let [predicate-fn v
          transform-fn (transformer k)
          transformed (transform-fn l r)]
      (if (= :error transformed)
        false
        (apply predicate-fn transformed)))))

(defn- ignore-diff? [l r rules]
  (some true? (map (partial ignore-diff-one-rule? l r) rules)))

(defn- diff-or-word
  "takes in two words. applies rules only if the words are different"
  [l r rules]
  (if (or (= l r) (ignore-diff? l r rules))
    l
    [l r]))

(defn- word-diffs
  "computes word difference between two lines
  ['hello world' 'goodbye world' {}] => [[hello goodbye] world]
  ['a' 'b' {'a' 'b'}] => (empty? ...)"
  [l r rules]
  (let [larr (.split l word-delimiter)
        rarr (.split r word-delimiter)]
    (map #(diff-or-word %1 %2 rules) larr rarr)))

(defn- contains-diff? [line]
  (some vector? line))

(defn- conj-if-diff [coll line]
  (if (contains-diff? line)
    (conj coll line)
    coll))


(comment
  (to-num-or-error "hello" "world")

  (Double/valueOf "123hello")
  
  (#(identity % %&) 1 2)
  (map identity {:a 1}) ; ([:a 1])
  (some true? [false])
  
  (seq {:a 1, :b 2})
  (seq (.split "        hello  \t  world              " "\\"))
  (seq (.split " hello " "\\s+"))
 
  (map + [1 2] [100 200])

  (difflog "hello world" "goodbye world")
  (conj [1 2] 3)
  (vector? [])
  (empty? (seq))
)
