(ns difflog.app
  (:refer-clojure :exclude [flatten])
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [difflog.domain :as domain]
            [difflog.files :as files])
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
  (str/join (System/lineSeparator) (map one-line-output
                                        (remove all-diff-ignored? diffs))))

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
