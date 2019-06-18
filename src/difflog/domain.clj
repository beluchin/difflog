(ns difflog.domain
  (:require [difflog.domain.internal :as internal]))

(defn difflog 
  "lhs, rhs: sequences of lines (line endings should be trimmed prior)
   rules:    a map of rules"
  [lhs rhs rules]
  (let [t-p (internal/trans-preds rules)]
    (map #(internal/difflogline-internal %1 %2 t-p) lhs rhs)))


(comment
  (seq (.split split-pattern "hello        world"))
  (line-word-diffs "hello world" "goodbye world" {})
  (partition-by #(string? %) [["a" "b"] " " "c"])
  (map #(if (string? (first %)) (string/join %) (first %))
       (partition-by #(string? %)
                     [["a" "b"] "           " "c"]))
  
  (str "a" "b")
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
