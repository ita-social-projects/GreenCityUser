FROM amazoncorretto:21.0.9 as runner
WORKDIR runner
COPY **/target/app.jar runner/
CMD java -jar runner/app.jar
