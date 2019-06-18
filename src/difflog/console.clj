(ns difflog.console
  (:require [difflog.console.app :as app]))

(defn difflog [args]
  (when-let [s (apply app/difflog args)] (println s)))
