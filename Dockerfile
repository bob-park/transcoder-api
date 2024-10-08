FROM ghcr.io/bob-park/ffmpeg-with-java
WORKDIR app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]