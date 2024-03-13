FROM bellsoft/liberica-openjdk-alpine

COPY /target/CarpRobot-0.0.1-SNAPSHOT.jar /carprobot.jar

CMD ["java", "-jar", "/carprobot.jar"]