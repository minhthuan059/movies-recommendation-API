# Tạo hình ảnh Gradle với JDK 21 để xây dựng ứng dụng
FROM gradle:8.11.1-jdk-21-and-23 AS build

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép mã nguồn vào container
COPY . /app

# Cấp quyền thực thi cho gradlew
RUN chmod +x ./gradlew

# Cài đặt và xây dựng ứng dụng
RUN ./gradlew bootJar --no-daemon

# Giai đoạn runtime
FROM openjdk:21-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Định nghĩa cổng ứng dụng
EXPOSE 8080

# Sao chép file JAR từ giai đoạn build
COPY --from=build /app/build/libs/*.jar /app/app.jar

COPY .env /app/.env
ENV export $(cat /app/.env | xargs)

# Khởi chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]
