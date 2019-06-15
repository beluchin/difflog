(ns difflog.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.string :as string]
            [difflog.domain :as domain]
            [clojure.edn :as edn]
            [clojure.string :as str])
  (:import (org.jline.terminal TerminalBuilder Terminal)
           (org.jline.reader LineReader LineReaderBuilder)))

(declare one-line-output normalize-line-endings)

(defn difflog
  ([lhs rhs] (difflog lhs rhs "{}"))
  ([lhs rhs rules]
   (output
    (domain/difflog (normalize-line-endings (slurp lhs))
                    (normalize-line-endings (slurp rhs))
                    (clojure.edn/read-string rules)))))

(defn output [diffs]
  (string/join (System/lineSeparator) (map one-line-output diffs)))

(defn interactive [& args]
  (let [term-builder (doto (TerminalBuilder/builder) (.system true))
        term (.build term-builder)
        reader (.. LineReaderBuilder (builder) (terminal term) (build))]
    (println (.getName term) (.getType term))
    (let [line (.readLine reader "hello world> ")]
      (.. term (writer) (println (str "====>" line)))
      (.flush term))))

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
  "['a' 'b'] ' c' => [-a-]{+b+} c"
  [line-diffs]
  (string/join (map flatten-if-diff line-diffs)))


(comment
  (str/replace "a\r\nb\n" #"(\r\n|\n)" (System/lineSeparator))
  
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
