(ns expectations-options
  (:require  [expectations :refer :all]))

(defn load-test-data
  {:expectations-options :before-run}
  []
  (println "Loading test db"))
