# Sử dụng Maven để build project
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Tạo thư mục làm việc trong container
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY pom.xml .
RUN mvn dependency:go-offline

# Build project và tạo file JAR
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------
# Tạo image từ JAR đã build
# -------------------------------
FROM eclipse-temurin:17-jdk

# Thư mục làm việc trong container mới
WORKDIR /app

# Sao chép file JAR từ giai đoạn builder
COPY --from=builder /app/target/book-store-be.jar .

# Khai báo port mà app sẽ chạy
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/book-store-be.jar"]
