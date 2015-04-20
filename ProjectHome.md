## Overview ##

The barcode reader project is intended to make tasks such as grocery shopping easier for those who are visually impaired. The application will allow individuals to scan an image of a unique barcode, and access information related to the item electronically. The product data will then be converted to a standard audio format, so that the user can have the information read aloud. By offering this service, we hope to make shopping a more practical activity for those who have trouble acquiring the information for themselves. It is important to note that the purpose of this project is to process the UPC image and return relevant product information. We designed this project with the assumption that the individual has the ability to find and take a picture of a particular UPC, whether through their own power, or with the help of a pre-existing detection program.

## Features ##

Our group decided to code the barcode project in J2ME (Java for mobile phones) because it is one of the most available mobile development platforms. Below is a description of what the application's major functions, broken into stages of the normative user cycle:

  1. A camera phone takes a picture of a UPC and sends it to the web server.
  1. The server processes in the image and returns the unique UPC number.
  1. This number is cross-referenced with pre-existing databases and product information is retrieved.
  1. Product information is returned as text, which is then converted to audio on the server.
  1. The audio file is sent back down to the phone, which the user can play to hear the information.

The application is written in Java, and makes use of an Apache TomCat Web Server running on aludra. To pass data between the client and server, we opted for Java Server Pages (JSP) which allow us to embed real Java code in HTML files.