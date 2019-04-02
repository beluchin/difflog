(ns difflog.domain)

(declare line-delimiter line-diff contains-diff?)
(defn difflog [lhs rhs]
  (let [l (.split lhs line-delimiter)
        r (.split rhs line-delimiter)]
    (remove (comp not contains-diff?) (map line-diff l r))))

(def ^:const line-delimiter (System/lineSeparator))
(def ^:const word-delimiter "\\s+")

(defn- diff-or-token [lt rt]
  (if (= lt rt) lt [lt rt]))

(defn- line-diff
  "computes difference between two lines
  > (line-diff 'hello world' 'goodbye world')
  [-hello-]{+goodbye+} world"
  [lhs rhs]
  (let [l (.split lhs word-delimiter)
        r (.split rhs word-delimiter)]
    (map diff-or-token l r)))

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
