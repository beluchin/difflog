(defproject difflog "0.1.0-SNAPSHOT"
  :description "smart log diff utility"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot difflog.core

  :dependencies [
                 ;; flycheck-clojure setup
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/core.typed "0.5.3"  :classifier "slim"]
                 [tortue/spy "1.4.0"]]
  
  :profiles {:uberjar {:aot :all}

             ;; Configuration here may be overridden by namespace metadata.
             :dev {:env {:squiggly {:checkers [:eastwood :typed :kibit]
                                    :eastwood-exclude-linters [:unlimited-use]
                                    :eastwood-options {:add-linters [:unused-fn-args]}}}}}
  ; to evaluate inside defproject ...
  ; https://stackoverflow.com/a/7739179/614800
  :target-path ~(str (let [os (System/getProperty "os.name")
                           is-windows (re-find #"(?i)windows" os)]
                       (if is-windows
                         "c:\\temp"
                         "/Users/beluchin/tmp"))
                     "/Users/beluchin/tmp"
                     "/difflog__/target")
  )
