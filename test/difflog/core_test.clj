(ns difflog.core-test
  (:require [clojure.test :as t]
            [difflog.core :as sut]
            [tempfile.core :as tf]))

(declare main)
(t/deftest e-to-e
  (tf/with-tempfile [lhsf (tf/tempfile "hello world")
                     rhsf (tf/tempfile "goodbye world")]
    (let [lhs (.getAbsolutePath lhsf)
          rhs (.getAbsolutePath rhsf)]
      
      (t/testing "without rules"
        (t/is (= "[-hello-]{+goodbye+} world" (main lhs rhs))))

      (t/testing "with rules - third argument"
        (t/is (empty? (main lhs rhs "{\"hello\" \"goodbye\"}")))))))

(defn- main
  [& args]
  (clojure.string/trim-newline
   (with-out-str
     (apply sut/-main args))))


(comment
  (with-out-str (println "this should return as a string"))
  (with-out-str (sut/-main "resources/lhs" "resources/rhs"))
  (with-out-str (apply sut/-main ["resources/lhs" "resources/rhs"]))
  (.getAbsolutePath (java.io.File. "."))
  (main "resources/lhs" "resources/rhs")
)
