FROM openjdk:17-jdk

COPY target/MailSender-1.0.jar /app/MailSender.jar

EXPOSE 9000

CMD ["java", "-jar", "/app/MailSender.jar"]

