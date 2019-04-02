(ns difflog.domain-test
  (:require [difflog.domain :as sut]
            [clojure.test :as t]))

(t/deftest difflog

  (t/testing "word difference"
    (t/is (= (sut/difflog "hello world" "goodbye world")
             [[["hello" "goodbye"] "world"]])))

  (t/testing "do not return identical lines by default"
    (t/is (empty? (sut/difflog "hello world" "hello world")))))
