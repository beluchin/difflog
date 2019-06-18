(ns difflog.console.interactive.domain
  (:require [difflog.domain.internal :as internal]))

(defn difflogline [lhs rhs rules]
  (internal/difflogline-internal lhs rhs (internal/trans-preds rules)))
