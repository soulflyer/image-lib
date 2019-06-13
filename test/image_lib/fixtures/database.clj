(ns image-lib.fixtures.database
  (:require [monger.collection :as mc]
            [monger.core :as mg]))


(def conn (atom  nil))
(def db-name "test")
(def images "images")
(def keywords "Keywords")
(def preferences "preferences")
(def tables [images keywords preferences])


(defn connect
  ([]
   (connect db-name))
  ([db-name]
   (reset! conn (mg/get-db (mg/connect) db-name))))


(defn empty-tables!
  ([]
   (empty-tables! db-name tables))
  ([db tables]
   (map #(mc/remove db %) tables)))


(defn connection
  ([]
   (connection db-name))
  ([db]
   (or @conn (connect db))))
