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

### Namespaces

Image-lib is now in several different namespaces.

#### image-lib.config

This is the database setup stuff, including the names of the tables used and a connection to the database. 

#### image-lib.helper

This contains various useful functions for manipulating records returned from the database. No database access is done here.

  **best** returns the last item of images when sorted by :Rating

  **clean-number-string** returns a number when given a string. 

  **image-path** return a string containing the year/month/project/version path of an image

  **image-paths** given a collection of pics, return just the paths

  **project-name** cuts the last part of the pathname off to leave yyyy/mm/project-name

  **project-year** returns the year from a project name in the form yyyy/mm/project

  **string-number-equals** a version of = that can compare numbers, strings or one of each

  **version-name** Cuts the extension off the end of a string

#### image-lib.file-helper

This contains various functions for accessing files in the local file system.

  **file-exists?** Not documented.
  
  **missing-files** Searches the directory given by root-path and returns a list of any images not found there but present in the image db. 
  
  **related-file-exists?** Not documented.
  
  **loosely-related-file-exists?** given a pathname to a file, checks if any variant of the file exists...
  
  **overwrite** Write the collection to a file, clearing it first
  
  **write** Append the collection 'things' to file 'file-name' one per line

#### image-lib.images

  **all-image-paths** Returns the path of every image in the database
  
  **find-image** returns an image given its id
  
  **find-images** Searches database collection for entries where the given field matches the given value
  
  **find-images-containing** Searches database collection for entries where the given field contains the given value
  
  **open-images** open the given images in an external viewer

#### image-lib.keywords

Contains functions for manipulating the table of keywords.

  **add-keyword** Add a new keyword
  
  **all-keywords** returns all the keyword ids
  
  **all-sub-keywords** Not documented.
  
  **delete-keyword** Remove a keyword
  
  **disconnect-keyword** Removes keyword from parent keyword but doesn't delete it
  
  **find-keyword** Not documented.
  
  **find-parents** given a keyword, returns a list of the parents
  
  **find-sub-keywords** given a keyword entry returns a list of all the sub keywords
  
  **move-keyword** Move a keyword from one parent to another
  
  **safe-delete-keyword** Delete a keyword, but only if it has no sub keywords

#### image-lib.preferences

Contains functions for accessing the preferences table

  **preference** return the value of the preference from the db
  
  **preference!** set the value of preference in the db
  
  **preferences** return all the preferences

#### image-lib.projects

Contains functions for accessing the projects table.

  **all-projects** returns a list of all the projects in yyyy/mm/project-name form

  **project-images** Returns all the images from a given project

  **project-paths** returns paths of all images in a given project

#### image-lib.search

Contains functions for building queries to search the database for images.

  **and** Not documented.

  **contains** returns true if haystack contains needle. 

  **eq** Not documented.

  **ge** Not documented.

  **gt** Not documented.

  **in** returns a sequence containing all entries of image-seq where meta-key contains meta-value

  **le** Not documented.

  **lt** Not documented.

  **or** Not documented.

#### image-lib.core

Contains the more complex functions used for manipulating the database. This section is likely to be changed.

##### missing-keywords

    (missing-keywords)
    
Returns a list of all the keywords found in the pictures collection that are not in the keywords collection.

##### add-missing-keywords

    (add-missing-keywords)

Adds the keywords found by missing-keywords to the db under the keyword "orphaned keywords"

##### orphaned-keywords

    (orphaned-keywords)
    
Returns a list of all the keywords found in the pictures collection that are not in the keywords collection.

##### add-orphaned-keywords

    (add-orphaned-keywords)

Adds the keywords found by missing-keywords to the db under the keyword "orphaned keywords"

## Use as a library

To add to a clojure project, add this to the ns defn

    :require [image-lib.core :refer :all]

and this to the project.clj file:

    [image-lib "0.2.2-SNAPSHOT"]
    
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

0.2.1-SNAPSHOT

0.2.2-SNAPSHOT Breaking changes. Namespace split into smaller pieces.

## License

Copyright Iain Wood © 2015-2017

Distributed under the Eclipse Public License version 1.0 
