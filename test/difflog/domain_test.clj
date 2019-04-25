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

(declare drop-last-arg)
(t/deftest rules
  (t/testing "word"
    (t/is (empty? (sut/difflog "a" "b" {"a" "b"})))
    (t/is (seq (sut/difflog "a" "b" {"b" "a"})))) ; not empty; not symmetrical

  (t/testing "numerical"
    (let [rule {:num (comp #(< % 0.1) #(Math/abs %) (drop-last-arg -))}]
      (t/is (empty? (sut/difflog "1.01" "1.02" rule)))
      (t/is (seq (sut/difflog "1.01 hello" "1.02 world" rule)))))

  (t/testing "one column"
    (t/is (empty? (sut/difflog "hello" "world" {:col 1})))
    (t/is (empty? (sut/difflog "hello world" "hello mundo" {:col 2}))))

  (t/testing "multiple columns"
    (t/is (empty? (sut/difflog "hello dear world" "goodbye dear mundo"
                               {:col #{1 3}})))))

(defn- drop-last-arg [fn]
  #(apply fn (drop-last %&)))

(comment
  ((comp #(< % 0.1) #(Math/abs %) (drop-last-arg -)) 1.01 1.02 :whatever)
  
  (sut/difflog "hello" "world" {:num (comp #(< % 0.1) #(Math/abs %) -)})
  (sut/difflog "world goodbye" "world hello")
  (sut/difflog "1.01 hello" "1.02 world" {:num (comp #(< % 0.1) #(Math/abs %) -)})
  (sut/difflog "1.01" "1.02" {:num (comp #(< % 0.1) #(Math/abs %) -)})
  
  ((comp #(< % 0.1) #(Math/abs %) -) 1.01 1.02) ; true
  ((comp #(< % 0.1) #(Math/abs %) -) 1 2) ; false

  (new BigDecimal "3.012345678901234567890") ;3.012345678901234567890M
  (Double/valueOf "3.012345678901234567890") ;3.0123456789012346
  (number? )
  (Math/abs -3)
  ((comp #(< % 0.1) #(Math/abs %) -) 1.01 1.02)
  )
