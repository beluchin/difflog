(ns difflog.console.interactive.app-test
  (:require [clojure.test :as t]
            [difflog.console.interactive.app :as sut]
            [tempfile.core :as f]))

(declare interactive)

(t/deftest nexts
  (do    
    (interactive "a1\na2" "b1\nb2")
    (t/is (= "[-a1-]{+b1+}" (sut/next)))
    (t/is (= "[-a2-]{+b2+}" (sut/next)))
    (t/is (= :no-more-diffs (sut/next)))))

(defn- interactive [lhstext rhstext]
  ;; duplicated in core-test
  (f/with-tempfile [l (f/tempfile lhstext)
                    r (f/tempfile rhstext)]
    (sut/interactive (.getAbsolutePath l) (.getAbsolutePath r))))
