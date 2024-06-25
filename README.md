## To make the implemented Java microservice in this repository accept RabbitMQ messages to send emails you need to have an already running service which is implemented here: [Secure-File-System-Web](https://github.com/vital-xvv/Secure-File-System-Web), which is a producer of the messages in Message Broker.

# Project Setup with Docker

1. Build an executable JAR archive of the implememnted Java mircoservice.

   Run `mvn clean package`

   After running this command you may see a newly created .jar file like this:

   ![.jar file](assets/executable_jar.png)

2. Later run this command to create an image of the implemented microservice in Docker.

   Run `docker build -t spring-mail-sender-app .`

   After a completion of the step you may notice a newly created image of the implemented Java microservice.

![image](assets/image.png)

3. Lastly run this command to pull all needed images of additional microcervices and create corresponding containers in Docker.

   Run `docker-compose up`

   After running this command all containers should have a "RUNNING" status:

![containers](assets/containers.png)

# Running Project in "dev" mode

1. Make sure before running the application in development mode you set up an active profile property in running configurations like this:

![dev_mode](assets/dev_mode.png)

2. You filled in missing corresponding properties from .env file in application-dev.yml that look like this:

![gaps](assets/gaps.png)

# Running integrational tests

### **Caution! To run integrational tests of the application make sure you have a Docker environment installed on your local machine to create testcontainers**.
