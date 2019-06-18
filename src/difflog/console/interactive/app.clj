(ns difflog.console.interactive.app
  (:refer-clojure :exclude [next])
  (:require [difflog.console.app :as app]
            [difflog.console.files :as files]
            [difflog.console.interactive.domain :as domain]))

(declare session difflogline)

(defn interactive [lhs rhs]
  (reset! session {:lhs (files/get-line-seq lhs)
                   :rhs (files/get-line-seq rhs)
                   :pos 0}))

(defn next []
  (let [pos (:pos @session)
        result (difflogline (nth (:lhs @session) pos)
                            (nth (:rhs @session) pos))]
    (swap! session update-in [:pos] inc)
    result))

(def ^:private session (atom nil))

(defn- difflogline [lhs rhs]
  (app/one-line-output (domain/difflogline (.trim lhs) (.trim rhs) {})))
