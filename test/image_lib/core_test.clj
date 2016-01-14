(ns image-lib.core-test
  (:require [image-lib.core :refer :all])
  (:use expectations))

(expect (= 1 1))

(expect (let [ar [1 2 3]]
   (first ar)) 2 )
