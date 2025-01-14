# Fullstack Java Project

## Sander Baens (3AONA)

Change the name and Class in the title above

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application

### Backend

**Running the application:**

1. start a rabbitmq docker container

2. start the services in the following order
   
   1. config-service
   
   2. discovery-service
   
   3. gateway-service
   
   4. messaging-service
   
   5. post-service
   
   6. review-service
   
   7. comment-service

### Frontend

**Running the application:**

- Run `npm install` and `ng build`

- Run `docker build -t fullstack-angular .` in a terminal in the same folder as the Dockerfile

- Then run `docker run -d -p 80:80 fullstack-angular` 

- Go to `http://localhost`

**Testing:**

- run `ng test`
