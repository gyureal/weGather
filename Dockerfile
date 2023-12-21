FROM gradle:7.6-jdk-alpine
WORKDIR /app

# 의존성 다운
ADD build.gradle /app/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true


COPY . /app
RUN gradle clean build --no-daemon -x test
CMD java -jar -Dspring.profiles.active=dev build/libs/*.jar
