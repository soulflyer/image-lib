(ns image-lib.play
  (:require [image-lib.config :refer   :all]
            [image-lib.core   :refer   :all]
            [image-lib.write  :refer   :all]
            [image-lib.keywords :refer :all]))

;; This is a good place to do db cleanup. The following commands were to remove a keyword
;; with a / in it that the API didn't like.

(safe-delete-keyword db keyword-collection "Hexagon grouper (Epinephelus merra/hexagonatus)")

(safe-delete-keyword db keyword-collection "epinephelus merra/hexagonatus")

(remove-keyword-from-photos
  db
  image-collection
  "Hexagon grouper (Epinephelus merra/hexagonatus)")
