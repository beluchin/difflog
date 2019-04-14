(ns difflog.app-test
  (:require [difflog.app :as sut]
            [clojure.test :as t]))

(t/deftest difflog
  (t/testing "plain output"
    (t/is (= "[-hello-]{+goodbye+} world"
             (sut/output [[["hello" "goodbye"] "world"]])))))
