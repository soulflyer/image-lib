(ns image-lib.projects
  (:require [image-lib.helper  :refer [image-path
                                       image-paths
                                       project-name]]
            [image-lib.images  :refer [all-image-paths]]
            [monger.collection :as mc]))

(defn project-images
  "Returns all the images from a given project"
  [database image-collection year month project]
  (mc/find-maps database image-collection {:Year year :Month month :Project project}))

(defn project-paths
  "returns paths of all images in a given project"
  [database image-collection year month project]
  (sort (map image-path (project-images database image-collection year month project))))

(defn all-projects
  "returns a list of all the projects in yyyy/mm/project-name form"
  [db image-collection]
  (sort (set (map project-name (all-image-paths db image-collection)))))
