(ns difflog.interactive
  (:require [difflog.files :as files]
            [difflog.app :as app]))

(declare session)

(defn interactive [lhs rhs]
  (reset! session {:lhs (files/slurp-log lhs)
                   :rhs (files/slurp-log rhs)}))

(defn next [] (app/difflogline (:lhs @session) (:rhs @session)))

(def ^:private session (atom nil))
