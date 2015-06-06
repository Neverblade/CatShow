# CatShow
CatShow is a simple slideshow program that displays pictures of cats on your screen, one new cat picture every 8 seconds.

Features:

     No internal memory - cat pictures are queried and received from "The Cat API", http://thecatapi.com/
     Multithreading to support the querying and loading of cat pictures, while simultaenously displaying them/listening for user input.
     Pause and skip forward buttons.
     A save to desktop image feature in case you find a picture you really like.
     
I wrote this program as an introduction in communicating with Web APIs, using URLs, etc. If there's anything here I'm proud of, it's the image buffering system that allows the slideshow to endlessly show pictures without stopping to load them.
     
