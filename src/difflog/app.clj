(ns difflog.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.string :as string]))

(declare one-line-output)
(defn output [diffs]
  (string/join (System/lineSeparator) (map one-line-output diffs)))

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
  (string/join " " (map flatten-if-diff line-diffs)))


(comment
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
