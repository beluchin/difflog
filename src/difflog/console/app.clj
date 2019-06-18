(ns difflog.console.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [difflog.console.files :as files]
            [difflog.domain :as domain]))

(declare output flatten-if-diff all-diff-ignored?)

(defn difflog
  ([lhs rhs] (difflog lhs rhs "{}"))
  ([lhs rhs rules]
   (output (domain/difflog (files/get-line-seq lhs)
                           (files/get-line-seq rhs)
                           (clojure.edn/read-string rules)))))

(defn one-line-output [line-diffs]
  (str/join (map flatten-if-diff line-diffs)))

(defn output [diffs]
  (let [ss (map one-line-output (remove all-diff-ignored? diffs))]
    (when (not= ss []) (str/join (System/lineSeparator) ss))))

(defn- all-diff-ignored? [line-diffs]
  (every? #(:ignored %) (remove string? line-diffs)))

(defn- flatten [{:keys [lhs rhs ignored]}]
  (if ignored
    (format "<%s><%s>" lhs rhs)
    (format "[-%s-]{+%s+}" lhs rhs)))

(defn- flatten-if-diff [x]
  (let [diff? map?] (if (diff? x) (flatten x) x)))


(comment
  (str/replace "a\r\nb\n" #"(\r\n|\n)" (System/lineSeparator))
  
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
