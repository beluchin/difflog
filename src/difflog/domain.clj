(ns difflog.domain)

(declare line-delimiter word-diffs contains-diff?)
(defn difflog
  ([lhs rhs] (difflog lhs rhs {}))
  ([lhs rhs rules]
   (let [l (.split lhs line-delimiter)
         r (.split rhs line-delimiter)]
     (remove (comp not contains-diff?) (map (partial word-diffs rules) l r)))))

(def ^:const line-delimiter (System/lineSeparator))
(def ^:const word-delimiter "\\s+")

(defn- ignore-diffs? [l r rules]
  (= (rules l) r))

(defn- diff-or-word
  "takes in two words"
  [rules l r]
  (if (or (= l r) (ignore-diffs? l r rules))
    l
    [l r]))

(defn- word-diffs
  "computes difference between two lines
  ['hello world' 'goodbye world' {}] => [[hello goodbye] world]
  ['a' 'b' {'a' 'b'}] => (empty? ...)"
  [rules lhs rhs]
  (let [l (.split lhs word-delimiter)
        r (.split rhs word-delimiter)]
    (map (partial diff-or-word rules) l r)))

(defn- contains-diff? [line]
  (some vector? line))

(defn- conj-if-diff [coll line]
  (if (contains-diff? line)
    (conj coll line)
    coll))


(comment
  (seq (.split "        hello  \t  world              " "\\"))
  (seq (.split " hello " "\\s+"))
 
  (map + [1 2] [100 200])

  (difflog "hello world" "goodbye world")
  (conj [1 2] 3)
  (vector? [])
  (empty? (seq))
)
