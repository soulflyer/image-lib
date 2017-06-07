# image-lib

A Clojure library designed to retrieve data about images and keywords from a mongo database.

## Usage

Although this project is intended as a library, it can be quite useful to directly manipulate the mongo database, particularly during development af a project making use of it. 

### Getting started

Using emacs and cider, do M-x cider-jack-in from the project file. This probably works fine from any file in the project directory but opening the project file makes it easy to be sure you are in the right clojure proj, especially when working in several at the same time.

Make sure the options are all set to the correct database and file paths. Each function can take parameters to specify these, but that will be a whole lot of extra typing you don't really want to have to do. Options are mostly set in the preferences collection of the database. The database and tables are specified in core.clj and it is probably easiest just to use the values there. They are reasonably sensible.

- database:               photos
- keyword collection:     keywords
- images collection:      images
- preferences collection: preferences

### Useful Functions

#### missing-keywords

    (missing-keywords)
    
Returns a list of all the keywords found in the pictures collection that are not in the keywords collection.

#### add-missing-keywords

    (add-missing-keywords)

Adds the keywords found by missing-keywords to the db under the keyword "orphaned keywords"

#### orphaned-keywords

    (orphaned-keywords)
    
Returns a list of all the keywords found in the pictures collection that are not in the keywords collection.

#### add-orphaned-keywords

    (add-orphaned-keywords)

Adds the keywords found by missing-keywords to the db under the keyword "orphaned keywords"

## Use as a library

To add to a clojure project, add this to the ns defn

    :require [image-lib.core :refer :all]

and this to the project.clj file:

    [image-lib "0.1.3-SNAPSHOT"]
    
## Soulflyer projects using image-lib

### find-images
http://github.com/soulflyer/find-images
Finds images based on simple exif/iptc data matches

### image-search
http://github.com/soulflyer/image-search
Finds images using a clojure repl. Complex search patterns are possible using and and or filters in any order. Also possible to open the results in a choosen image viewer.

### find-projects
http://github.com/soulflyer/find-projects
Finds a complete list of projects

### check-images
http://github.com/soulflyer/check-images
given a pathname, checks that every image in the db is present under that path.

### keywords
http://github.com/soulflyer/find-pics
This is a standalone keyword browser. It allows for creating and rearranging keywords, browsing the keyword heirarchy, and viewing pictures with selected keywords. It can also output a list of images for further processing.

## Versions

0.1.0-SNAPSHOT Initial version.
0.1.1-SNAPSHOT Functions now take a database rather than a string so every call doesn't have to re-establish a connection to mongo
0.1.2-SNAPSHOT
0.1.3-SNAPSHOT added fns to deal with orphaned keywords

## License

Copyright Iain Wood © 2015-2016

Distributed under the Eclipse Public License version 1.0 
