(ns difflog.domain)

(declare delimiter diff-or-token conj-if-diff)
(defn difflog [lhs rhs]
  (let [lts (.split lhs delimiter)
        rts (.split rhs delimiter)]
    (reduce conj-if-diff [] [(map diff-or-token lts rts)])))

(def ^:const delimiter "\\s+")

(defn- diff-or-token [lt rt]
  (if (= lt rt) lt [lt rt]))

(defn- contains-diff? [line]
  (some vector? line))

(defn- conj-if-diff [coll line]
  (if (contains-diff? line)
    (conj coll line)
    coll))

(comment
 (seq (.split "        hello  \t  world              " "\\s+"))
 (seq (.split " hello " "\\s+"))
 
 (map + [1 2] [100 200])

 (difflog "hello world" "goodbye world")
 (conj [1 2] 3)
 (vector? [])
 (empty? (seq))
 )
