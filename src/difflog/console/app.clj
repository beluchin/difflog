(ns difflog.console.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [difflog.console.files :as files]
            [difflog.domain :as domain])
  (:import org.jline.reader.LineReaderBuilder
           org.jline.terminal.TerminalBuilder))

(declare output one-line-output all-diff-ignored?)

(defn difflog
  ([lhs rhs] (difflog lhs rhs "{}"))
  ([lhs rhs rules]
   (output (domain/difflog (files/get-line-seq lhs)
                           (files/get-line-seq rhs)
                           (clojure.edn/read-string rules)))))

(defn difflogline [lhs rhs]
  (one-line-output (domain/difflogline (.trim lhs) (.trim rhs) {})))

(defn output [diffs]
  (let [ss (map one-line-output (remove all-diff-ignored? diffs))]
    (when (not= ss []) (str/join (System/lineSeparator) ss))))

(defn interactive [& args]
  (let [term-builder (doto (TerminalBuilder/builder) (.system true))
        term (.build term-builder)
        reader (.. LineReaderBuilder (builder) (terminal term) (build))]
    (println (.getName term) (.getType term))
    (let [line (.readLine reader "hello world> ")]
      (.. term (writer) (println (str "====>" line)))
      (.flush term))))

(defn- all-diff-ignored? [line-diffs]
  (every? #(:ignored %) (remove string? line-diffs)))

(defn- flatten [{:keys [lhs rhs ignored]}]
  (if ignored
    (format "<%s><%s>" lhs rhs)
    (format "[-%s-]{+%s+}" lhs rhs)))

(defn- flatten-if-diff [x]
  (let [diff? map?] (if (diff? x) (flatten x) x)))

(defn- one-line-output [line-diffs]
  (str/join (map flatten-if-diff line-diffs)))


(comment
  (str/replace "a\r\nb\n" #"(\r\n|\n)" (System/lineSeparator))
  
  (sequential? [])
  (sequential? '())
  (sequential? "")
  )
