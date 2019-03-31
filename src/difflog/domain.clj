(ns difflog.domain)

(declare delimiter diff-or-token)

(defn difflog [lhs rhs]
  (let [lts (.split lhs delimiter)
        rts (.split rhs delimiter)]
    (map diff-or-token lts rts)))

(def ^:const delimiter "\\s+")

(defn- diff-or-token [lt rt]
  (if (= lt rt) lt [lt rt]))


(comment
 (seq (.split "        hello  \t  world              " "\\s+"))
 (seq (.split " hello " "\\s+"))
 
 (map + [1 2] [100 200])
 )
