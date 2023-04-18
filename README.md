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

## Documentation

Please check out
my [Mock UI](https://www.figma.com/proto/uAzAfoRI7c7ws5HD4tL87N/Banana-Rescue?node-id=6-975&scaling=min-zoom&page-id=0%3A1&starting-point-node-id=2%3A22)
on Figma used for my MVP!  
Also there is an overview of my [Models](https://www.figma.com/file/spoKUhcg5Y7VZIhLy2M0dt/Banana-Rescue-Models),
including JSON representation.  
And maybe most importantly, here is
my [API Documentation](https://app.swaggerhub.com/apis-docs/VALENTINFINKE/BananaRescue) on SwaggerHub.

## Installation & Running

In order to run *BANANA RESCUE* in your environment, you will need to do the following:

* clone the repo
* set up the following environment variables for the backend:
  * `MONGO_URI=mongodb://[SERVER]:[PORT]`
  * `CLOUDINARY_URL=cloudinary://[API_KEY]:[API_SECRET]@[CLOUD_NAME]`
* start the backend with Maven
* `cd` into the frontend folder and run: `npm ci`
* start the frontend with: `npm start`

Please be aware that using the User's location when setting up the Radar in most Browsers requires an HTTPS connection,
so for these environments that feature will not work in development mode.  
I deliberately didn't set the `HTTPS=true` flag in the `start` command as this breaks the WebSocket connections.  
This caveat only applies to the development environment, not production!
