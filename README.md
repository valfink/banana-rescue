# Welcome to BANANA RESCUE

Banana Rescue is my capstone project for the coding bootcamp *Web Development with Java and TypeScript* at
*neuefische*.  
The app helps users to reduce food waste by sharing what they are not able to consume.

If you have food that you want to share, you can take a picture of it, put it in the app together with some infos – the
name, until when you need it to be picked up, until when it can be consumed, where it is located, a comment – and the
other users can write you a message in order to pick it up.

## Technology

The backend is written in **Java** using **Spring Boot** with **Spring Security**.  
The frontend is written in **TypeScript** using **React**. The styling is all **custom vanilla CSS**.  
The chat is implemented using **STOMP over WebSocket**.

## Installation & Running

In order to run *BANANA RESCUE* in your environment, you will need to do the following:

* download the repo
* set up the following environment variables for the backend:
  * `MONGO_URI=mongodb://[SERVER]:[PORT]`
  * `CLOUDINARY_URL=cloudinary://[API_KEY]:[API_SECRET]@[CLOUD_NAME]`
* start the backend with Maven
* `cd` into the frontend folder and run: `npm ci`
* start the frontend with: `npm start`

## More infos to follow...!
