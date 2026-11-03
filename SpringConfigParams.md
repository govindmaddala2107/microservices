# Spring Boot Parameter Passing Guide

Spring Boot applications can receive configuration and parameters from multiple sources. Spring Boot follows a specific property precedence order, where higher-priority sources override lower-priority sources.

---

# 1. application.properties / application.yml

## application.yml

```yaml
server:
  port: 8080

app:
  name: DemoApp
```

Access:

```java
@Value("${app.name}")
private String appName;
```

Run:

```bash
java -jar app.jar
```

---

# 2. Profile-Specific Configuration

## application-dev.yml

```yaml
server:
  port: 8081
```

Run:

```bash
java -jar app.jar --spring.profiles.active=dev
```

Or

```bash
java -Dspring.profiles.active=dev -jar app.jar
```

---

# 3. Command-Line Arguments (--)

Highest commonly used priority.

Run:

```bash
java -jar app.jar --server.port=9090 --app.name=CLIApp
```

Access:

```java
@Value("${app.name}")
private String appName;
```

Value:

```text
CLIApp
```

---

# 4. JVM System Properties (-D)

Set before `-jar`.

Run:

```bash
java -Dserver.port=9090 \
     -Dapp.name=JvmApp \
     -jar app.jar
```

Access:

```java
@Value("${app.name}")
private String appName;
```

Value:

```text
JvmApp
```

---

# 5. Environment Variables

## Linux / Mac

```bash
export SERVER_PORT=9090
export APP_NAME=EnvApp

java -jar app.jar
```

## Windows CMD

```cmd
set SERVER_PORT=9090
set APP_NAME=EnvApp

java -jar app.jar
```

## PowerShell

```powershell
$env:SERVER_PORT="9090"
$env:APP_NAME="EnvApp"

java -jar app.jar
```

Spring automatically maps:

```text
APP_NAME
```

to

```text
app.name
```

and

```text
SERVER_PORT
```

to

```text
server.port
```

---

# 6. SPRING_APPLICATION_JSON

Useful in Docker and Kubernetes.

Linux:

```bash
export SPRING_APPLICATION_JSON='{
  "server": {
    "port": 9090
  },
  "app": {
    "name": "JsonApp"
  }
}'

java -jar app.jar
```

Windows:

```cmd
set SPRING_APPLICATION_JSON={"server":{"port":9090}}
java -jar app.jar
```

---

# 7. External application.yml

Keep configuration outside JAR.

Directory Structure:

```text
config/
 └── application.yml
```

Run:

```bash
java -jar app.jar \
     --spring.config.location=file:./config/
```

Or:

```bash
java -jar app.jar \
     --spring.config.additional-location=file:./config/
```

---

# 8. Spring Cloud Config Server

application.yml

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

Config Server URL:

```text
http://localhost:8888/{application}/{profile}
```

Example:

```text
http://localhost:8888/h2jpa/default
```

---

# 9. OS Arguments in IDE

## IntelliJ IDEA

### Program Arguments

```text
--server.port=9090
--app.name=IdeaApp
```

### VM Options

```text
-Dserver.port=9090
-Dapp.name=IdeaVmApp
```

---

# 10. Docker Environment Variables

Run:

```bash
docker run \
  -e SERVER_PORT=9090 \
  -e APP_NAME=DockerApp \
  my-app
```

---

# 11. Kubernetes ConfigMap

ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config

data:
  APP_NAME: K8sApp
```

Deployment:

```yaml
env:
  - name: APP_NAME
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: APP_NAME
```

---

# 12. Kubernetes Secret

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: password
```

---

# 13. Java Main Method Arguments

Run:

```bash
java -jar app.jar one two three
```

Read:

```java
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {

        for (String arg : args) {
            System.out.println(arg);
        }

        SpringApplication.run(DemoApplication.class, args);
    }
}
```

Output:

```text
one
two
three
```

---

# Reading Properties

## @Value

```java
@Value("${app.name}")
private String appName;
```

---

## @ConfigurationProperties

application.yml

```yaml
app:
  name: Demo
  version: 1.0
```

Java

```java
@ConfigurationProperties(prefix = "app")
@Component
@Data
public class AppProperties {

    private String name;
    private String version;
}
```

---

# Common JVM Parameters

Heap Size:

```bash
-Xms512m
-Xmx2g
```

GC:

```bash
-XX:+UseG1GC
```

Debug:

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

Encoding:

```bash
-Dfile.encoding=UTF-8
```

Timezone:

```bash
-Duser.timezone=Asia/Kolkata
```

Example:

```bash
java \
 -Xms512m \
 -Xmx2g \
 -Dapp.name=ProdApp \
 -Duser.timezone=Asia/Kolkata \
 -jar app.jar \
 --server.port=9090
```

---

# Property Precedence (Highest → Lowest)

```text
1. Command-line arguments (--key=value)

2. Java System Properties (-Dkey=value)

3. SPRING_APPLICATION_JSON

4. Environment Variables

5. External application.yml/properties

6. Packaged application.yml/properties

7. @PropertySource

8. Default Properties (SpringApplication.setDefaultProperties)
```

---

# Quick Examples

## JVM Property

```bash
java -Dapp.name=JvmApp -jar app.jar
```

## Environment Variable

```bash
export APP_NAME=EnvApp
java -jar app.jar
```

## Command Line

```bash
java -jar app.jar --app.name=CliApp
```

## Cloud Config

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

## External Config

```bash
java -jar app.jar --spring.config.location=file:/opt/config/
```

---

# Recommended Production Pattern

```text
Source Code
    ↓
application.yml (defaults)
    ↓
Spring Cloud Config Server
    ↓
Environment Variables
    ↓
JVM (-D)
    ↓
Command Line Args
```

This provides centralized configuration while still allowing environment-specific and runtime overrides.