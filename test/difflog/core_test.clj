(ns difflog.core-test
  (:require [clojure.test :as t]
            [difflog.core :as sut]
            [tempfile.core :as f]
            [clojure.string :as str]))

(declare main multiline)

(t/deftest basic
  (let [lhs "hello world"
        rhs "goodbye world"]
    (t/testing "without rules"
      (t/is (= "[-hello-]{+goodbye+} world" (main lhs rhs))))

    (t/testing "with rules - third argument"
      (t/is (empty? (main lhs rhs "{\"hello\" \"goodbye\"}")))))

  (t/testing "[ and ] are delimiters"
    (t/is (= "[[-a-]{+b+}]" (main "[a]" "[b]")))))

(t/deftest line-separator-on-input
  (t/testing "\r\n as line separator"
    (t/is (= (multiline "hello [-world-]{+mundo+}"
                        "[-hello-]{+hola+} world")
             (main (str/join "\r\n"  ["hello world"
                                      "hello world"])
                   (str/join "\r\n" ["hello mundo"
                                     "hola world"])))))

  (t/testing "\n as line separator"
    (t/is (= (multiline "hello [-world-]{+mundo+}"
                        "[-hello-]{+hola+} world")
             (main (str/join "\n"  ["hello world"
                                    "hello world"])
                   (str/join "\n" ["hello mundo"
                                   "hola world"]))))))

(defn- multiline [& strings]
  (clojure.string/join (System/lineSeparator) strings))

(defn- main [lhstext rhstext & args]
  ;; duplicated in interactive-test
  (f/with-tempfile [l (f/tempfile lhstext)
                    r (f/tempfile rhstext)]
    (clojure.string/trim-newline
     (with-out-str
       (apply (partial sut/-main
                       (.getAbsolutePath l)
                       (.getAbsolutePath r))
              args)))))


(comment
  (clojure.string/join (System/lineSeparator) ["a" "b"])
  
  (with-out-str (println "this should return as a string"))
  (with-out-str (sut/-main "resources/lhs" "resources/rhs"))
  (with-out-str (apply sut/-main ["resources/lhs" "resources/rhs"]))
  (.getAbsolutePath (java.io.File. "."))
  (main "resources/lhs" "resources/rhs")
)
