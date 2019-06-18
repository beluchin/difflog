(ns difflog.domain-test
  (:require [clojure.string :as string]
            [clojure.test :as t]
            [difflog.domain :as sut]
            [difflog.domain.num :as num]))

(t/deftest difflog
  (t/testing "word difference"
    (t/is (= [[{:lhs "hello" :rhs "goodbye" :ignored false}  " world"]]
             (sut/difflog ["hello world"] ["goodbye world"] {}))))

  (t/testing "identical lines"
    (t/is (let [s "hello world"] (= [[s]] (sut/difflog [s] [s] {})))))

  (t/testing "multilines"
    (t/is (let [first-line "first line is identical"]
            (= [[first-line]
                ["second has " {:lhs "one" :rhs "una" :ignored false} " difference"]]
               (sut/difflog [first-line "second has one difference"]
                            [first-line "second has una difference"]
                            {}))))))


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
