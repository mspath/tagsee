# Tagsee

Little app which displays a directory of photos. Photos can be assigned to tags.

----

to keep things simple the app makes following assumptions:

the base url of the webserver must be known at compile time.
when a user enters his name a json file at `baseurl/username/gallery.json` providing a list of all image-urls.
the current implementation generates it by listing all images the `/PUBLIC/username/images/` directory of the accompanying express.js instance. 

----

Environment

- Kotlin 1.6.10
- Android Studio Bumblebee 2021.1.1
- Gradle Plugin 7.1.2

----

updated: 2022-04-06