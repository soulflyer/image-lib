(ns image-lib.utils.database.seed
  (:require [image-lib.utils.database :refer [connection]]
            [monger.collection :as mc]))


(defn preferences

  ([]
   (preferences (connection)))

  ([db]
   (println "Seeding Preferences")
   (mc/insert-batch db "preferences" [{:_id  "masters-directory"
                                       :path "/Users/iain/Pictures"}])))


(defn images

  ([]
   (images (connection)))

  ([db]
   (println "Seeding images")
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


(defn keywords

  ([]
   (keywords (connection)))

  ([db]
   (println "seeding keywords")
   (mc/insert-batch db "keywords" [{:_id "people" :sub ["Kathryn" "Rachael" "me"]}
                                   {:_id "Kathryn" :sub []}
                                   {:_id "Iain" :sub []}
                                   {:_id "Rachael" :sub []}
                                   {:_id "me" :sub ["Iain"]}
                                   {:_id "Root" :sub ["people"]}])))
