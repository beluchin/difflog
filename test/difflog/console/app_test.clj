(ns difflog.console.app-test
  (:require [difflog.console.app :as sut]
            [clojure.test :as t]))

(t/deftest difflog
  (t/testing "plain output"
    (t/is (= "[-hello-]{+goodbye+} world"
             (sut/output [[{:lhs "hello" :rhs "goodbye" :ignored false} " world"]])))))
