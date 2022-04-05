FROM gradle:jdk16 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM gradle:jre16

RUN mkdir /app

WORKDIR /app

COPY --from=build /home/gradle/src/build/distributions/*.tar /app/kotlin-async.tar

RUN tar -xvf kotlin-async.tar

ENTRYPOINT ["./kotlin-async-1.0-SNAPSHOT/bin/kotlin-async"]