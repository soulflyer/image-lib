(ns expectations-options
  (:require  [expectations :refer :all]
             [clojure.java.io :as io]
             [monger
              [collection :as mc]
              [core :as mg]]))

(defn load-test-data
  {:expectations-options :before-run}
  []
  (let [db (mg/get-db (mg/connect) "test")]
    (let [wrtr (io/writer "/tmp/clojure-test.txt")]
      (.write wrtr "you can delete me\n")
      (.close wrtr))

    (println "Loading test db")
    (mc/remove db "preferences")
    (mc/remove db "keywords")
    (mc/remove db "images")
    (mc/insert-batch db "preferences" [{:_id  "masters-directory"
                                        :path "/Users/iain/Pictures"}])
    (mc/insert-batch db "keywords" [{:_id "people" :sub ["Kathryn" "Iain" "Rachael"]}
                                    {:_id "Kathryn" :sub []}
                                    {:_id "Iain" :sub []}
                                    {:_id "Rachael" :sub []}])
    (mc/insert-batch db "images" [{:_id "1"
                                   :Year "1983"
                                   :Month "01"
                                   :Project "15-Test-Project"
                                   :Version "DIW_001"
                                   :Keywords ["Kathryn"]
                                   :Rating "4.0"}
                                  {:_id "2"
                                   :Year "1981"
                                   :Month "10"
                                   :Project "18-Test-Project"
                                   :Version "DIW_002"
                                   :Keywords ["Rachael"]
                                   :Rating "5.0"}
                                  {:_id "3"
                                   :Year "1958"
                                   :Month "10"
                                   :Project "12-Test-Project"
                                   :Version "DIW_001"
                                   :Keywords ["Iain"]
                                   :rating "3.0"}
                                  {:_id "4"
                                   :Year "1958"
                                   :Month "10"
                                   :Project "12-Test-Project"
                                   :Version "DIW_002"
                                   :Keywords ["Iain"]
                                   :rating "4.0"}])))
