######################
##   build target   ##
######################
FROM openjdk:8u212-jdk-alpine3.9 as build

WORKDIR /app
RUN apk --no-cache add bash curl openssl openssh

COPY gradle/wrapper ./gradle/wrapper
COPY gradlew ./
RUN ./gradlew --no-daemon --version

COPY build.gradle gradle.properties settings.gradle ./
COPY gradle/*.gradle ./gradle/
COPY .git ./.git/

## Build the application fat jar, invalidate only if the source changes
COPY src/main ./src/main
RUN ./gradlew --no-daemon shadowJar

## run the static analysis and tests
COPY codenarc.groovy ./
COPY src/test ./src/test
RUN ./gradlew --no-daemon check

## build args required for coveralls reporting
ARG TRAVIS
ARG TRAVIS_JOB_ID

## run code coverage report, send to coveralls when executing in Travis CI
RUN ./gradlew --no-daemon jacocoTestReport coveralls

######################
##  package target  ##
######################
FROM openjdk:8u212-jre-alpine3.9 as package

## setup env var for the app name
ENV CRATEKUBE_APP cratekube

## add in files needed at runtime
WORKDIR /app
RUN apk --no-cache add bash curl openssl openssh &&\
    curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.16.3/bin/linux/amd64/kubectl &&\
    mv kubectl /usr/local/bin/kubectl &&\
    chmod +x /usr/local/bin/kubectl &&\
    curl -L -o rke_linux-amd64  https://github.com/rancher/rke/releases/download/v0.3.2/rke_linux-amd64 &&\
    mv rke_linux-amd64 /usr/local/bin/rke &&\
    chmod +x /usr/local/bin/rke

COPY entrypoint.sh ./
COPY --from=build /app/build/libs/${CRATEKUBE_APP}-*-all.jar /app/${CRATEKUBE_APP}.jar
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["-h"]
