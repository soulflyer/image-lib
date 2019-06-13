(ns image-lib.preferences-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.fixtures :as fixtures]
            [image-lib.preferences :as sut]
            [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]))

(use-fixtures :once
  fixtures/database )


(deftest preferences
  (expect
    (sut/preference (database/connection) database/preferences "masters-directory")
    "/Users/iain/Pictures"))
