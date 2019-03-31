(ns difflog.app
  (:refer-clojure :exclude [flatten]))

(defn difflog [lhs rhs])

(declare flatten-if-diff)
(defn output [diffline]
  (clojure.string/join " " (map flatten-if-diff diffline)))

(defn- flatten [[lhs rhs]]
   (format "[-%s-]{+%s+}" lhs rhs))

(defn- flatten-if-diff [x]
  (let [diff? sequential?]
    (if (diff? x)
      (flatten x)
      x)))


(comment
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
