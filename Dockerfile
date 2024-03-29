## 1st stage

FROM eclipse-temurin:11.0.16.1_1-jdk-jammy AS build

COPY . /src
WORKDIR /src
RUN set -eux; \
    ./gradlew bootJar; \
    java -Djarmode=layertools -jar build/libs/*.jar extract --destination /dist

## 2nd stage

FROM eclipse-temurin:17.0.4.1_1-jre-jammy

EXPOSE 8080
WORKDIR /app

RUN set -eux; \
    groupadd -r -g 999 app; \
    useradd -r -u 999 -g app -l -d /app app

COPY --from=build /dist/dependencies/ .
COPY --from=build /dist/spring-boot-loader/ .
COPY --from=build /dist/snapshot-dependencies/ .
COPY --from=build /dist/application/ .

USER app
ENTRYPOINT exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher
