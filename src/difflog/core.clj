(ns difflog.core
  (:gen-class)
  (:require [difflog.app :as app]))

(declare difflog)
(defn -main
  [& args]
  (if (= "-i" (first args))
    (apply app/interactive (rest args))
    (print (apply app/difflog args))))


(comment
  (apply app/difflog ["resources/lhs" "resources/rhs"])
  
  (apply (fn [x y]) [1 2 3])

  (clojure.edn/read-string "{\"a\" \"b\"}")

  ;; Hello World - https://github.com/google/diff-match-patch/wiki/Language:-Java
  (import 'name.fraser.neil.plaintext.diff_match_patch)
  (def tool (diff_match_patch.))
  (def diff (.diff_main tool "Hello World" "Goodbye World"))
  (.diff_cleanupSemantic tool diff)
  (.println System/out diff)

  (def lhs (slurp "resources/lhs"))
  (def rhs (slurp "resources/rhs"))
  (def diff (.diff_main tool lhs rhs))
  (.diff_cleanupSemantic tool diff)
  (.println System/out diff)
  )
