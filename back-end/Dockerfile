FROM gradle:jdk19 as build
WORKDIR /app

COPY gradlew .
COPY gradle gradle

COPY build.gradle .

RUN ./gradlew dependencies --offline

COPY src src

RUN ./gradlew build --no-tests

FROM openjdk:19-rc-oraclelinux8

ARG DEPENDENCY=/app/build/libs

COPY --from=build ${DEPENDENCY}/*.jar /app/lib/

ENTRYPOINT ["java", "-cp", "app/lib/*", "cs.vsu.ReportingGenerationService.ReportingGenerationService"]
