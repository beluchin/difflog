(ns difflog.domain-test
  (:require [difflog.domain :as sut]
            [clojure.test :as t]
            [clojure.string :as string]))

(t/deftest basic

  (t/testing "word difference"
    (t/is (= [[["hello" "goodbye"] "world"]]
             (sut/difflog "hello world" "goodbye world"))))

  (t/testing "do not return identical lines by default"
    (t/is (empty? (sut/difflog "hello world" "hello world"))))

  (t/testing "multilines"
    (let [join (partial string/join (System/lineSeparator))]
      (t/is (= [["second" "has" ["one" "una"] "difference"]]
               (sut/difflog (join  ["first line is identical" 
                                    "second has one difference"])
                            (join  ["first line is identical"
                                    "second has una difference"])))))))

(t/deftest rules
  (t/testing "ignore certain word differences"
    (t/is (empty? (sut/difflog "a" "b" {"a" "b"})))))
