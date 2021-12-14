# 基础镜像
FROM openjdk:18-jdk-alpine

RUN mkdir /app
WORKDIR /app

RUN apk --no-cache add curl
COPY  target/project-0.0.1.jar /app

# 暴露端口
EXPOSE 8080

CMD ["java", "-jar", "project-0.0.1.jar"]
