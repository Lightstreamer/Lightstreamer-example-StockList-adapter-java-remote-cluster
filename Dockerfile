# First stage: Maven-based image to build the jar
FROM maven:3-eclipse-temurin-11 as builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

# Package the jar
RUN mvn -f /usr/src/app/pom.xml clean package

# Second stage: Final java-based image to run the jar
FROM eclipse-temurin:11
# Copy the jar file from the first stage
COPY --from=builder  /usr/src/app/target/example-StockList-adapter-java-remote-0.1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/stocklist-adapter-remote.jar
ENTRYPOINT ["java", "-jar", "/usr/app/stocklist-adapter-remote.jar"]
CMD ["-metadata_rrport", "6663", "-data_rrport", "6661"]
