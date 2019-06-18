(ns difflog.console.interactive.app-test
  (:require [clojure.test :as t]
            [difflog.console.interactive.app :as sut]
            [tempfile.core :as f]))

(declare interactive)

(t/deftest nexts
  (t/testing "first"
    (t/is (= "[-a-]{+b+}" (do (interactive "a" "b")
                              (sut/next)))))
  (t/testing "multiple"
    (t/is (= "[-a2-]{+b2+}" (do (interactive "a1\na2" "b1\nb2")
                                (sut/next)
                                (sut/next)))))
    
  (t/testing "next skips over lines with no differences" ,,,)
  (t/testing "previous" ,,,))

(defn- interactive [lhstext rhstext]
  ;; duplicated in core-test
  (f/with-tempfile [l (f/tempfile lhstext)
                    r (f/tempfile rhstext)]
    (sut/interactive (.getAbsolutePath l) (.getAbsolutePath r))))
