# Web Application for NextTrack's Recommendation Algorithm

## Overview
This project consists of two main components:
1. **Frontend**: Built with [Next.js](https://nextjs.org/)
2. **Backend**: Built with [Spring Boot](https://spring.io/projects/spring-boot)

## Prerequisites
Make sure you have the following installed before proceeding:
- **Git**: [Download and install](https://git-scm.com/)
- **Node.js** (for Next.js frontend): [Download and install](https://nodejs.org/)
- **Java 23+** (for Spring Boot backend): [Download and install](https://adoptopenjdk.net/)
- **Maven** (for building the backend): [Download and install](https://maven.apache.org/)

## Cloning the Repository
To clone the repository, open a terminal and run:

```sh
git clone https://github.com/NextTrack-WAT-ai/Web-Application.git
cd your-repo
```

## Setting up the frontend
Navigate to frontend folder 
```sh 
cd frontend
```

Install dependencies 
```sh 
npm install 
```

Run development build
```sh
npm run dev 
```

The next.js app should now be running on http://localhost:3000.

## Setting up the backend
Navigate to root backend directory
```sh 
cd ../spring-boot-app
```

Do a clean build with Maven 
```sh 
mvn clean install 
```

Run the spring-boot app
```sh 
mvn spring-boot:run
```

The backend should now be running at http://localhost:8080.

## MongoDB setup
Ensure that you have [MongoDB installed](https://www.mongodb.com/docs/mongodb-shell/install/).

Also ensure that the mongoDB service is running as outlined in the docs above. 

Navigate to the application.properties file on the backend 
```sh
cd ../spring-boot-app/src/main/resources/application.properties
```

Ensure that your application.properties file looks like the following:
```properties
spring.application.name=spring-boot-app
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=nexttrack
```

Start mongoDB locally
```sh
mongosh
```

Create the nexttrack db
```sh
use nexttrack
```

Navigate to the nexttrack db
```sh
db nexttrack
```

You should now have mongoDB set up!