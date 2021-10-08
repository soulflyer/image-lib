# Introduction

image-lib is a Clojure library designed to retrieve data about images and keywords from a mongo database.

## Usage

This is a library for inclusion in other projects. It can be run alone, using the cofig.clj file to set up connections to the database. However it is easier to use from within another project. Checkout http://github.com/soulflyer/image-search for an easy introduction. This contains both a standalone commandline utility and a repl based search. It also has some handy useage information.

## Getting started

Make sure the options are all set to the correct database and file paths. Each function can take parameters to specify these, but that will be a whole lot of extra typing you don't really want to have to do. Options are mostly set in the preferences collection of the database. The database and tables are specified in core.clj and it is probably easiest just to use the values there. They are reasonably sensible.

- database:               photos
- keyword collection:     keywords
- images collection:      images
- preferences collection: preferences

