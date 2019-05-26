(ns image-lib.config
  (:require [monger.core :as mg]))

(def database                   "photos")
(def keyword-collection       "keywords")
(def preference-collection "preferences")
(def image-collection           "images")
;; The following are constants so thet we don't have to make a new connection for every
;; call to the db. Uncomment them only for dev work. Normally the code that calls image-lib
;; will set these.
(def connection (mg/connect))
(def db (mg/get-db connection database))
