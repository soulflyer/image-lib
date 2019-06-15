(ns image-lib.preferences-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.preferences :as sut]
            [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]
            [image-lib.utils.fixtures :as fixtures]))


(use-fixtures :once
  fixtures/database fixtures/seed-preferences)


(deftest preferences
  (expect
    (sut/preference (database/connection) database/preferences "masters-directory")
    "/Users/iain/Pictures"))
