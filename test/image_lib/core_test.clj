(ns image-lib.core-test
  (:require [image-lib.core        :as ic]
            [image-lib.helper      :as ih]
            [monger [core :as mg]]
            ;; TODO Need to switch to clojure-test expectations but that means using deftest first
            ;;[expectations.clojure.test :refer :all]
            [expectations :refer :all]            ))

;; (use-fixtures :once ??)

(def db (mg/get-db (mg/connect) "test"))


(expect (ih/image-path (ic/best-image db "images" "Iain"))
  "1958/10/12-Test-Project/DIW_002.jpg")
