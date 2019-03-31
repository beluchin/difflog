(ns difflog.core
  (:gen-class)
  (:require [difflog.app :as app]
            [difflog.domain :as domain]))

(defn -main
  [& args]
  (println (app/output (domain/difflog (slurp (first args))
                                       (slurp (second args))))))


(comment
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
