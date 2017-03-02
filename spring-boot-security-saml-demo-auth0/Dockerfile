FROM lwieske/java-8:jdk-8u77-slim
EXPOSE 8080
VOLUME /tmp
ADD target/spring-boot-security-saml-demo-auth0-1.3-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]