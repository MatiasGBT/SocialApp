# SocialApp
(In process) Social network made with Angular, Spring Boot (Java), Keycloak and MySQL.  

***Note:** this is a personal practice project and I do not allow its distribution.*  
## API Docs
To view the API documentation you must first [deploy](#deployment) the application and then enter one of the following paths in your browser:
- http://localhost:8090/api-docs (To view the docs in JSON format)
- http://localhost:8090/swagger-ui.html (To view the docs with a graphical interface)

## Deployment
***Important note:** At the moment the app starts in developer mode and is not adapted for production.*  

The application can be deployed using a single command thanks to Docker.  
First of all, download [Docker Desktop](https://www.docker.com/products/docker-desktop/) if you don't have it installed locally. 

Then you have to clone this repository:  
`git clone https://github.com/MatiasGBT/SocialApp.git`  
or download it as a ZIP file.

After that, you will be able to deploy this app running the following command in
the repository folder:  
`docker compose up`  
You will need to have Docker Desktop open and running when you execute this command (and any other Docker command).


The first time you run the application it will be slow because Docker needs to download and build the images specified in the `docker-compose.yml` file.  
You will be able to know when the project is ready when all the containers compile correctly. You will see the next lines in log of each container:
#### backend:  
`SocialAppApiApplication: Started SocialAppApiApplication in 42.05 seconds (JVM running for 69.204)`
#### frontend:  
`✔ Compiled successfully.`
#### mysql:  
`/usr/sbin/mysqld: ready for connections. Version: '8.0.30'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server - GPL.`
#### keycloak:  
`Running the server in development mode. DO NOT use this configuration in production.`  

*Note: You can see the log of each container clicking on their names on Docker Desktop.*

*Note 2: To deploy the application you need the following ports to be free on your PC: 3036, 4200, 8080 and 8090.*

When all the containers are built, you will be able to open the application by putting `http://localhost:4200` in your browser.
## Getting started
![Login page](https://i.imgur.com/xsfxohn.png)
After opening the app (see [Deployment](#deployment)) click on **New user? Register** to create an account (or sign in with Google).  
You will be redirected to a registration page. Use false information (just to test):
- First name: *user*
- Last name: *test*
- Email: *user@mail.com*
- Username: *user*
- Password: *user123*
- Confirm password: *user123*  
After registering you will be able to use and explore the app.
## Screenshots
Some screenshots of the application.
![Index Page](https://i.imgur.com/WQYpktP.png)
*Index page*

![Profile Page](https://i.imgur.com/wQwLP15.png)
*Profile page*

![Friend's Profile Page](https://i.imgur.com/7Mlbtut.png)
*Friend's profile page*

![Edit Profile Page](https://i.imgur.com/km8SgLO.png)
*Edit profile page*

![Chat Page](https://i.imgur.com/Q6L3cBD.png)
*Chat page*

![Is Calling You](https://i.imgur.com/z5zt714.png)
*Modal that pops up when a friend calls you*

![Call Page](https://i.imgur.com/rYDxN4B.png)
*Call page*

![Notifications Page](https://i.imgur.com/gsrM6Ll.png)
*Notifications page*

![Settings Page](https://i.imgur.com/aoJ08aL.png)
*Settings page*
## Tech Stack

**Client:** Angular

**Server:** Java, Spring Boot

**Authorization server:** Keycloak

**Database:** MySQL
## External utilities
Utilities provided by other developers/designers used in this project:

- [keycloak-angular](https://www.npmjs.com/package/keycloak-angular)
- [ngx-translate](https://github.com/ngx-translate/core)
- [ngx-dropzone](https://www.npmjs.com/package/ngx-dropzone)
- [ngx-photo-editor](https://www.npmjs.com/package/ngx-photo-editor)
- [Font Awesome 6](https://fontawesome.com/)
- [Sweet Alert 2](https://sweetalert2.github.io/)
- [Angular Material](https://material.angular.io/)
- [unDraw](https://undraw.co/illustrations)
## Author

[@MatiasGBT](https://github.com/MatiasGBT)