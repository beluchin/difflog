(ns difflog.core-test
  (:require [clojure.test :as t]
            [difflog.core :as sut]
            [tempfile.core :as tf]))

(t/deftest e-to-e
  (t/testing "the whole thing is properly wired"
    (t/is (= "[-hello-]{+goodbye+} world"
             (tf/with-tempfile [lhs (tf/tempfile "hello world")
                                rhs (tf/tempfile "goodbye world")]
               (with-out-str (sut/-main (.getName lhs)
                                        (.getName rhs))))))))
