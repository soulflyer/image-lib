# image-lib

A Clojure library designed to retrieve data about images and keywords from a mongo database.

## Usage

To add to a clojure project, add this to the ns defn

    :require [image-lib.core :refer :all]

and this to the project.clj file:

    [image-lib "0.1.0-SNAPSHOT"]
    
## Soulflyer projects using image-lib

### find-images
http://github.com/soulflyer/find-images
Finds images based on simple exif/iptc data matches

### find-projects
http://github.com/soulflyer/find-projects
Finds a complete list of projects

### check-images
http://github.com/soulflyer/check-images
given a pathname, checks that every image in the db is present under that path.

## Versions

0.1.0-SNAPSHOT Initial version.
0.1.1-SNAPSHOT Functions now take a database rather than a string so every call doesn't have to re-establish a connection to mongo

## License

Copyright Iain Wood © 2015-2016

Distributed under the Eclipse Public License either version 1.0 
