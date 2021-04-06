## 1st stage

FROM gradle:jdk11 AS build

COPY . /src
WORKDIR /src
RUN gradle bootJar

## 2nd stage

FROM gcr.io/distroless/java:11

EXPOSE 8080
WORKDIR /app
CMD ["app.jar"]

COPY --from=build /src/build/libs/*.jar app.jar
