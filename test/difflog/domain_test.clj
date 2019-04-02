(ns difflog.domain-test
  (:require [difflog.domain :as sut]
            [clojure.test :as t]))

(t/deftest difflog

  (t/testing "word difference"
    (t/is (= [[["hello" "goodbye"] "world"]]
             (sut/difflog "hello world" "goodbye world"))))

  (t/testing "do not return identical lines by default"
    (t/is (empty? (sut/difflog "hello world" "hello world"))))

  (t/testing "multilines"
    (t/is (= [["second" "has" ["one" "una"] "difference"]]
             (sut/difflog "first line is identical\nsecond has one difference"
                          "first line is identical\nsecond has una difference")))))
