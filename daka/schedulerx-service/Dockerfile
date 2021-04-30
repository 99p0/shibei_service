FROM adoptopenjdk:8-jdk-openj9
ADD target/app.jar app.jar
ENTRYPOINT [ \
    "java", \
    "-XX:MetaspaceSize=256m", \
    "-XX:MaxMetaspaceSize=256m", \
    "-Xms2048m", \
    "-Xmx2048m", \
    "-Xmn256m", \
    "-Xss256k", \
    "-XX:SurvivorRatio=8", \
    "-XX:+UseConcMarkSweepGC", \
    "-Duser.timezone=GMT+08", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Xshareclasses", \
    "-Xquickstart", \
    "-jar", \
    "/app.jar", \
    "--spring.profiles.active=prod" \
]
