(ns difflog.files
  (:require [clojure.string :as str]
            [difflog.domain :as domain]))

(declare normalize-line-endings)

(defn slurp-log [filename]
  (normalize-line-endings (slurp filename)))

(defn get-line-seq [filename]
  (.split (slurp-log filename) (System/lineSeparator)))

(defn- normalize-line-endings [s]
  (str/replace s #"\r\n|\n" domain/line-delimiter))
