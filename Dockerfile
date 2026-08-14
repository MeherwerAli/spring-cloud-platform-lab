FROM maven:3.9.11-eclipse-temurin-17 AS build

ARG MODULE
WORKDIR /workspace
COPY . .
RUN mvn --batch-mode --no-transfer-progress -pl "${MODULE}" -am package -DskipTests \
    && find "${MODULE}/target" -maxdepth 1 -type f -name '*.jar' -exec cp '{}' /tmp/app.jar \; \
    && test -s /tmp/app.jar

FROM eclipse-temurin:17-jre

RUN useradd --system --uid 10001 spring
USER spring
COPY --from=build /tmp/app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
