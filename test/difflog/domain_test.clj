(ns difflog.domain-test
  (:require [clojure.string :as string]
            [clojure.test :as t]
            [difflog.domain :as sut]
            [difflog.domain.num :as num]))

(declare drop-last-arg)

(t/deftest difflog
  (t/testing "word difference"
    (t/is (= [[{:lhs "hello" :rhs "goodbye" :ignored false}  " world"]]
             (sut/difflog ["hello world"] ["goodbye world"]))))

  (t/testing "identical lines"
    (t/is (let [s "hello world"] (= [[s]] (sut/difflog [s] [s])))))

  (t/testing "multilines"
    (t/is (let [first-line "first line is identical"]
            (= [[first-line]
                ["second has " {:lhs "one" :rhs "una" :ignored false} " difference"]]
               (sut/difflog [first-line "second has one difference"]
                            [first-line "second has una difference"]))))))

(t/deftest difflogline
  (t/testing "word difference"
    (t/is (= [{:lhs "a" :rhs "b" :ignored false}]
             (sut/difflogline "a" "b" {})))
    (t/is (= [{:lhs "hello" :rhs "goodbye" :ignored false} " world"]
             (sut/difflogline "hello world" "goodbye world" {}))))

  (t/testing "identical lines"
    (t/is (= ["hello world"]
             (sut/difflogline "hello world" "hello world" {})))))

(t/deftest rules
  (t/testing "word"
    (t/is (= [{:lhs "a" :rhs "b" :ignored true}]
             (sut/difflogline "a" "b" {"a" "b"})))
    (t/is (= [{:lhs "a" :rhs "b" :ignored false}]
             (sut/difflogline "a" "b" {"b" "a"})))) ; not symmetrical

  (t/testing "numerical"
    (let [rule-spec {:num (comp #(< % 0.1) #(Math/abs %) (drop-last-arg -))}]
      (t/is (= [{:lhs "1.01" :rhs "1.02" :ignored true}]
               (sut/difflogline "1.01" "1.02" rule-spec)))
      (t/is (= [{:lhs "1.01" :rhs "1.02" :ignored true}
                " "
                {:lhs "hello" :rhs "world" :ignored false}]
               (sut/difflogline "1.01 hello" "1.02 world"
                                {:num (num/diff-no-larger-than 0.1)})))))

  (t/testing "one column"
    (t/is (= ["hello " {:lhs "world" :rhs "mundo" :ignored false}]
             (sut/difflogline "hello world" "hello mundo" {:col 1})))
    (t/is (= ["hello " {:lhs "world" :rhs "mundo" :ignored true}]
             (sut/difflogline "hello world" "hello mundo" {:col 3}))))

  (t/testing "multiple columns"
      (t/is (= [{:lhs "hello" :rhs "goodbye" :ignored true}
                " dear "
                {:lhs "world" :rhs "mundo" :ignored true}]
               (sut/difflogline "hello dear world" "goodbye dear mundo"
                            {:col #{1 5}})))))

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
