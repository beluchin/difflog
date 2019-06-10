(ns difflog.interactive-test
  (:require  [clojure.test :as t]
             [difflog.interactive :as sut]
             [tempfile.core :as f]))

(declare interactive)

(t/deftest interactive-session
  (interactive "a" "b")
  (t/testing "next" (t/is "[-a-]{+b+}" (sut/next)))
  (t/testing "next skips over lines with no differences" ,,,)
  (t/testing "previous" ,,,))

(defn- interactive [lhstext rhstext]
  ;; duplicated in core-test
  (f/with-tempfile [l (f/tempfile lhstext)
                    r (f/tempfile rhstext)]
    (sut/interactive (.getAbsolutePath l) (.getAbsolutePath r))))
