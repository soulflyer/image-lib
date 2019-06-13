(ns image-lib.preferences-test
  (:require [clojure.test :refer :all]
            [image-lib.preferences :as sut]
            [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]
            [image-lib.fixtures :as fixtures]))


(use-fixtures :once
 fixtures/database )

(deftest blah
  (is (= 0 0)))
