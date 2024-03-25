FROM docker.io/eed3si9n/sbt:jdk11-alpine

WORKDIR /src/main/scala
COPY . /src/main/scala
RUN sbt update
EXPOSE 8000
ENTRYPOINT ["sbt", "main/run"]