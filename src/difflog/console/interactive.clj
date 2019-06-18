(ns difflog.console.interactive
  (:import org.jline.reader.LineReaderBuilder
           org.jline.terminal.TerminalBuilder))

(defn interactive [& args]
  (let [term-builder (doto (TerminalBuilder/builder) (.system true))
        term (.build term-builder)
        reader (.. LineReaderBuilder (builder) (terminal term) (build))]
    (println (.getName term) (.getType term))
    (let [line (.readLine reader "hello world> ")]
      (.. term (writer) (println (str "====>" line)))
      (.flush term))))
