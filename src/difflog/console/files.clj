(ns difflog.console.files
  (:require [clojure.string :as str]
            [difflog.domain :as domain]))

(declare slurp-log)

(defn get-line-seq [filename]
  (.split (slurp-log filename) (System/lineSeparator)))

(defn- normalize-line-endings [s]
  (str/replace s #"\r\n|\n" domain/line-delimiter))

(defn- slurp-log [filename]
  (normalize-line-endings (slurp filename)))
