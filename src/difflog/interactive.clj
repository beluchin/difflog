(ns difflog.interactive
  (:require [difflog.files :as files]
            [difflog.app :as app]))

(declare session)

(defn interactive [lhs rhs]
  (reset! session {:lhs (files/line-seq lhs)
                   :rhs (files/line-seq rhs)
                   :pos 0}))

(defn next []
  (let [pos (:pos @session)
        result (app/difflogline (nth (:lhs @session) pos)
                                (nth (:rhs @session) pos))]
    (swap! session update-in [:pos] inc)
    result))

(def ^:private session (atom nil))
