# 阶段一：编译打包（这个阶段最终不保留，产物只有 jar）
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml settings.xml ./
RUN mvn -s settings.xml dependency:go-offline -B
COPY src ./src
RUN mvn -s settings.xml package -DskipTests -B

# 阶段二：只带 JRE 运行，镜像体积小一半以上
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]