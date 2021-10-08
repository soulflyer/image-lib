(defproject image-lib "0.2.5-SNAPSHOT"
  :description "A collection of functions to retrieve data about images and keywords from a mongo database"
  :url "http://github.com/soulflyer/image-lib"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.novemberain/monger "3.5.0"]
                 [org.clojure/clojure "1.10.1"]]
  :profiles {:dev {:dependencies [[expectations/clojure-test "1.1.1"]]
                   :plugins [[autodoc/lein-autodoc "1.1.1"]
                             [lein-codox "0.10.7"]]
                   :codox {:output-path  "docs/new"
                           :themes [:default :imagelib]}}})
