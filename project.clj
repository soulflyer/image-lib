(defproject image-lib "0.2.4"
  :description "A collection of functions to retrieve data about images and keywords from a mongo database"
  :url "http://github.com/soulflyer/image-lib"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.novemberain/monger "3.1.0"]
                 [org.clojure/clojure "1.9.0"]]
  :profiles {:dev {:dependencies [[expectations "2.1.9"]
                                  [expectations/clojure-test "1.1.1"]]}})
