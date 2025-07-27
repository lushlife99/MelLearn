FROM amazoncorretto:17-alpine-jdk

ARG PROFILES
ARG ENV

# Elastic APM Agent 추가
ENV APM_AGENT_VERSION=1.51.0
ADD https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/${APM_AGENT_VERSION}/elastic-apm-agent-${APM_AGENT_VERSION}.jar /elastic-apm-agent.jar

COPY build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java \
  -javaagent:/elastic-apm-agent.jar \
  -Delastic.apm.service_name=spring-app \
  -Delastic.apm.server_urls=http://apm-server:8200 \
  -Delastic.apm.environment=${ENV} \
  -Delastic.apm.application_packages=com.example \
  -Dspring.profiles.active=${PROFILES} \
  -jar app.jar"]
