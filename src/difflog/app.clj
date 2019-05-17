(ns difflog.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.string :as string]
            [difflog.domain :as domain]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(declare one-line-output)
(defn output [diffs]
  (string/join (System/lineSeparator) (map one-line-output diffs)))

(declare normalize-line-endings)
(defn difflog
  ([lhs rhs] (difflog lhs rhs "{}"))
  ([lhs rhs rules]
   (output
    (domain/difflog (normalize-line-endings (slurp lhs))
                    (normalize-line-endings (slurp rhs))
                    (clojure.edn/read-string rules)))))

(defn- normalize-line-endings [s]
  (str/replace s #"\r\n|\n" domain/line-delimiter))

(defn- flatten [[lhs rhs]]
   (format "[-%s-]{+%s+}" lhs rhs))

(defn- flatten-if-diff [x]
  (let [diff? sequential?]
    (if (diff? x)
      (flatten x)
      x)))

(defn- one-line-output
  "['a' 'b'] 'c' => [-a-]{+b+} c"
  [line-diffs]
  (string/join "" (map flatten-if-diff line-diffs)))


(comment
  (str/replace "a\r\nb\n" #"(\r\n|\n)" (System/lineSeparator))
  
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
