FROM maven:4.0.0-openjdk-23 as maven
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn package -DskipTests=true
WORKDIR /app/target/dependency
RUN jar -xf ../employeemanagementservice.jar

FROM ibm-semeru-runtimes:open-23-jre-centos7
ARG DEPENDENCY=/app/target/dependency
COPY --from=maven ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=maven ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=maven ${DEPENDENCY}/BOOT-INF/classes /app
CMD java -server -Xmx1024m -Xss1024k -XX:MaxMetaspaceSize=135m -XX:CompressedClassSpaceSize=28m -XX:ReservedCodeCacheSize=13m -XX:+IdleTuningGcOnIdle -Xtune:virtualized -cp app:app/lib/* com.nutech.nutech.config