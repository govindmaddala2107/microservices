# Microservices

## Spring Cloud Config and H2 JPA
- Let's say now we have config files in some cloud or git, then we have to get on to one server and other clients will use them.

### Spring Cloud Server and setup:
- Create server with ```spring-cloud-config-server``` dependency.
- In ```ConfigserverApplication``` file, add **@EnableConfigServer** annotation.
- In application.yml, add like 
  ```yaml
  spring:
    application:
      name: configserver

    cloud:
      config:
        server:
          git:
            uri: https://github.com/govindmaddala2107/app-config
            
            # If git repo is private add username and password
            username: ${GIT_USER}
            password: ${GIT_TOKEN}

    logging:
      level:
        org.springframework.cloud.config.server: ERROR

    server:
      port: 888
  ```
- If git repo is private add username and password and hard code the value in yml file or pass from envi variables like below mentioned image:
  - GIT_USER and GIT_TOKEN has to be passed from ![ENVI Variables](images/enviVariables.png)
  

#### Git setup 
  - Also create some config.yml fine in git [for example] [app-config](https://github.com/govindmaddala2107/app-config) repo in git with yml files with names exactly equal app names of microservices:
    - for example ```spring.application.name=h2jpa``` file name should be 
      - default: h2jpa.yml
      - dev: h2jpa-dev.yml
      - prod: h2jpa-prod.yml

##### Accessing endpoints of :
  - [default h2Jpa yml](http://localhost:888/h2jpa/default)
    ```json
    {
      "name": "h2jpa",
      "profiles": [
        "default"
      ],
      "label": null,
      "version": "e016839489ea9dbc4b12c8a3695c484d96a263f0",
      "state": "",
      "propertySources": [
        {
          "name": "https://github.com/govindmaddala2107/app-config/h2jpa.yml",
          "source": {
            "spring.application.name": "h2jpa",
            "spring.h2.console.enabled": true,
            "spring.datasource.url": "jdbc:h2:mem:testdb",
            "build.id": 102,
            "build.version": "v3",
            "build.date": 31102026,
            "server.servlet.context-path": "/api/public"
          }
        }
      ]
    }
    ```
    - [dev h2Jpa yml](http://localhost:888/h2jpa/dev)
      ```json
      {
        "name": "h2jpa",
        "profiles": [
          "dev"
        ],
        "label": null,
        "version": "6f2fe9d5040970e096abdd97e6b2ec985a861d12",
        "state": "",
        "propertySources": [
          {
            "name": "https://github.com/govindmaddala2107/app-config/h2jpa-dev.yml",
            "source": {
              "spring.application.name": "h2jpa",
              "spring.h2.console.enabled": true,
              "spring.datasource.url": "jdbc:h2:mem:dev",
              "build.id": 101,
              "build.version": "dev1",
              "build.date": 31102026,
              "server.servlet.context-path": "/api/public"
            }
          },
          {
            "name": "https://github.com/govindmaddala2107/app-config/h2jpa.yml",
            "source": {
              "spring.application.name": "h2jpa",
              "spring.h2.console.enabled": true,
              "spring.datasource.url": "jdbc:h2:mem:testdb",
              "build.id": 102,
              "build.version": "v3",
              "build.date": 31102026,
              "server.servlet.context-path": "/api/public"
            }
          }
        ]
      }
      ```


### Spring Cloud Client and setup:
- Now here we want to access details from server.
- Dependencies needed are:
  - spring-cloud-starter-config [for client setup]
  - spring-boot-starter-actuator [for refresh the server without downtime and to fetch latest changes in config]
- Code Setup:
  - Before:
    ```yaml
    spring:
      application:
        name: h2jpa
      config:
        import: configserver:http://localhost:888
      h2:
        console:
          enabled: true
      datasource:
        url: jdbc:h2:mem:testdb
    ```
  - After Config Client setup:
    ```yaml
    spring:
      application:
        name: h2jpa
      config:
        import: configserver:http://localhost:888
    server:
      servlet:
        context-path: /api/public

    # It is to enable refresh server with actuator
    management:
        endpoints:
          web:
            exposure:
              include: refresh
    ```
- Now let's say config values changed in git, now using acuator, we can hit refresh api to restart the server and can hit the api to get updated values:
  - To create this scenario, I have created an api to fetch build details. 
  - In git, in h2jpa.yml I kept:
    ```yaml
    build:
    id: 102
    version: v3
    date: 31102026
    ```
    - [Build Info](http://localhost:8080/api/public/build-info) returns
      ```json
      {
        "id": 102,
        "version": "v3",
        "date": 31102026
      }
      ```
  - Now I have updated values in git, in h2jpa.yml like 
    ```yaml
    build:
    id: 103
    version: updated4
    date: 31102026
    ```
  - Now on hitting [Build Info](http://localhost:8080/api/public/build-info) returns same 
    ```json
    {
      "id": 102,
      "version": "v3",
      "date": 31102026
    }
    ```
  - To get updated values, we have to restart h2jpa app in general, but with spring actuator refresh, it is very easy:
    - hit POST call [Actuator Refresh](http://localhost:8080/api/public/actuator/refresh) with request body and we will get changes in response like below:
      ```json
      [
        "config.client.version",
        "build.version",
        "build.id",
        "build.date"
      ]
      ```
  - Now on hitting building-info, i will get updated data:
    ```json
    {
      "id": 103,
      "version": "updated4",
      "date": 31102026
    }
    ```

## Storing Configurations in Database for Dynamic Updates:
  - Here we can store configuration settings in database and can use.
  - Dependencies needed are:
    - ```JDBC API ```
      - Database Connectivity API that defines how a client may connect and query a database.
    - ```PostgreSQL Driver [Any SQL Database]```
      - A JDBC and R2DBC driver that allows Java programs to connect to a PostgreSQL database using standard, database independent Java code.
  - Config Server application.yml settings are as follow:
    ```yaml
    spring:
      application:
        name: configserver
      profiles:
        active: jdbc
      datasource:
        url: jdbc:postgresql://localhost:5432/configdb
        username: postgres
        password: postgres
        driver-class-name: org.postgresql.Driver
      cloud:
        config:
          server:
            jdbc:
              sql: >
                SELECT PROP_KEY, PROP_VALUE
                FROM PROPERTIES
                WHERE APPLICATION=? AND PROFILE=? AND LABEL=?
              default-label: main
    ```
  - spring.profiles.active should be **jdbc** when we use database for configuration, else springboot will consider git.

## File-Based Configuration:
- Also we can keep file based configuration and it is called as **native** configuration.
- #### Cloud Server Setup:
  - create **config** folder in ```configserver > src > main > resources > config``` and create files for project **h2jpa** like:
    - h2jpa.yml [keep content of h2jpa.yml file present in git]
    - h2jpa-dev.yml [keep content of h2jpa-dev.yml file present in git]
      - i.e file name should be alike application name.
  - Now in **application.yml** keep config like:
    ```yaml
    spring:
      application:
        name: configserver
      profiles:
        active: native # to tell to use local file system
      cloud:
        config:
          server:
            native:
              search-locations: classpath:/config # to check config folder in resources
    logging:
      level:
        org.springframework.cloud.config.server: ERROR

    server:
      port: 888
    ```
- #### Cloud Client Setup:
  - Now in client, delete all *.yml files and keep **application.yml** and keep
    ```yml
    spring:
      application:
        name: h2jpa
      config:
        import: configserver:http://localhost:888
      profiles:
        active: dev
    ```
  - [Build-Info-With-Files](http://localhost:8080/api/public/build-info) returns like 
    - In General, it returns content from h2jpa.yml
      ```json
      {
        "id": 101,
        "version": "NativeDefault",
        "date": 31102026
      }
      ```
    - for spring.profiles.active = "dev", returns content from h2jpa-dev.yml
      ```json
      {
        "id": 101,
        "version": "NativeDev",
        "date": 31102026
      }
      ```
    
  

- #### Dynamic updation of Config using RabbitMQ:
  - ##### RabbitMQ setup:
    - ###### Setup in Machine
      - RabbitMQ can be installed locally but using docker we can install it by 
        ```powershell
        # latest RabbitMQ 4.x
        docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
        ```
      - rabbitmq runs on port 5674 and rabbitmq-management runs on 15672.
      - default credentials are guest and guest.
    - ###### Setup in Springboot project:
      - Dependency need is 
        ```xml
        <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
        ```
      - setup in application.yml is
        ```yaml
        spring
            rabbitmq:
              host: localhost
              port: 5672
              username: guest
              password: guest

        management:
          endpoints:
            web:
              exposure:
                include: "*" # to include /busrefresh 
        ```
        - this could subscribe our springboot project to rabbitmq.
    

  - ##### Other required configuration for Dynamic updation:
    - ##### Config Server setup: 
      - **Dependencies needed:** 
        - Actuator
        - Spring Boot Starter AMQP
        ```xml
        <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
        ```
        - Same dependencies are needed for **Config Client** also.
      - **Config setup:**
        - Before setup is 
          ```yaml
          spring:
            application:
              name: configserver
            profiles:
              active: native
            cloud:
              config:
                server:
                  native:
                    search-locations: file:///C:\Users\EDIUS X\Desktop\gomad\microservices\microservices_config\configserver\src\main\resources\config
          #          search-locations: classpath:/config
            rabbitmq:
              host: localhost
              port: 5672
              username: guest
              password: guest

          management:
            endpoints:
              web:
                exposure:
                  include: "*"
          server:
            port: 8888
          ```
        - for dynamic updation classpath should not be used because on application startup, it will bundle the config files when we configure with classpath.
        - So for this, configuration will be like: **file:** followed by **///** and **full path of config** folder.
      
    - ##### COnfig Client Setup:
      - **Dependencies needed:**
        - actuator and Spring Boot Starter AMQP
      - **Config setup:**
        - Almost no changes but only we add actuator endpoints inclusion and rabbitmq subscription
        ```yaml
        spring:
          application:
            name: orders
          config:
            import: optional:configserver:http://localhost:8888
          rabbitmq:
            host: localhost
            port: 5672
            username: guest
            password: guest

        management:
          endpoints:
            web:
              exposure:
                include: "*"

        server:
          port: 8082
        ```
    - ###### API Endpoints using:
      - [Orders Config details-Dev](http://localhost:8888/orders/dev)
        ```json
        {
          "name": "orders",
          "profiles": [
            "dev"
          ],
          "label": null,
          "version": null,
          "state": null,
          "propertySources": [
            {
              "name": "file:/C:/Users/EDIUS X/Desktop/gomad/microservices/microservices_config/configserver/src/main/resources/config/orders-dev.yml",
              "source": {
                "build.id": 101,
                "build.version": "NativeDev",
                "build.date": 1086486
              }
            },
            {
              "name": "file:/C:/Users/EDIUS X/Desktop/gomad/microservices/microservices_config/configserver/src/main/resources/config/orders.yml",
              "source": {
                "build.id": 101,
                "build.version": "NativeDefault",
                "build.date": 1086486
              }
            }
          ]
        }
        ```
      - [Orders Built Info](http://localhost:8082/build-info)   
        - Active profile is default
        ```json
        {
          "id": 101,
          "version": "NativeDefault",
          "date": 1086486,
          "service": "orders"
        }
        ```
      - Let's say version is changed from "NativeDefault" to "NativeDefaultUpdated"
        - Automatically in config server, it will update, no need of refresh but to get updated one in Clients, you need to run **/busrefresh** on either client or server [anything works fine].
      - Now make sure docker is running and run POST method [BusRefresh](http://localhost:8888/actuator/busrefresh) and [Orders Built Info](http://localhost:8082/build-info) will return 
        ```json
        {
          "id": 101,
          "version": "NativeDefaultUpdated",
          "date": 1086486,
          "service": "orders"
        }
        ```
    - ###### Dependencies nee
## Spring Cloud Config Encryption (`encrypt.key`)

##### Why do we need `encrypt.key`?

In real-world applications, configuration files often contain sensitive information such as:

- Database passwords
- API keys
- OAuth client secrets
- SMTP passwords
- Third-party service credentials

Storing these values in plain text inside Git repositories is a security risk.

Spring Cloud Config Server provides encryption support so that sensitive values can be stored in encrypted form inside the Config Repository.

---

##### Configuration

```yaml
encrypt:
  key: my-secret-encryption-key
```

Example:

```yaml
encrypt:
  key: Govind@2026#SecretKey
```

The Config Server uses this key to:

- Encrypt plain text values
- Decrypt encrypted values before sending them to clients

---

##### How It Works

###### Step 1: Encrypt a Secret

Request:

```http
POST /encrypt
Content-Type: text/plain

mypassword
```

Response:

```text
AQBXCDFGH123456...
```

---

###### Step 2: Store in Config Repository

Instead of:

```yaml
spring:
  datasource:
    password: mypassword
```

Store:

```yaml
spring:
  datasource:
    password: "{cipher}AQBXCDFGH123456..."
```

Commit the file to Git.

---

###### Step 3: Client Fetches Configuration

Client Application:

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

When the client requests configuration:

```text
http://localhost:8888/myapp/default
```

Config Server automatically decrypts:

```yaml
password: "{cipher}AQBXCDFGH123456..."
```

and returns:

```yaml
password: mypassword
```

The client never needs to perform decryption.

---

###### Real-Time Example

###### Config Repository

```yaml
spring:
  datasource:
    username: postgres
    password: "{cipher}AQBXCDFGH123456..."
```

###### Config Server

```yaml
encrypt:
  key: Govind@2026#SecretKey
```

###### Client Application

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

Result:

```java
@Value("${spring.datasource.password}")
private String password;
```

Value received:

```text
mypassword
```

even though Git contains only the encrypted value.

---

###### Security Benefits

✅ Passwords are not stored in plain text

✅ Developers cannot easily view production secrets

✅ Safe to keep encrypted values in Git

✅ Centralized secret management

✅ Applications receive decrypted values transparently

---

###### Best Practice

- ###### Avoid Hardcoding
  - Instead of:
  ```yaml
  encrypt:
    key: my-secret-key
  ```

Use:

```yaml
encrypt:
  key: ${ENCRYPT_KEY}
```

Windows:

```cmd
set ENCRYPT_KEY=MySuperSecretKey
```

Linux:

```bash
export ENCRYPT_KEY=MySuperSecretKey
```

---



##### Changing the Key

If you change:

```yaml
encrypt:
  key: old-key
```

to

```yaml
encrypt:
  key: new-key
```

all previously encrypted values become invalid and must be encrypted again.

---

##### Use Strong Keys

Bad:

```yaml
encrypt:
  key: password
```

Good:

```yaml
encrypt:
  key: G0v!nd@2026#ConfigServer$Secret
```

---

##### Typical Production Usage

```text
Git Repository
     │
Encrypted Secrets
     │
     ▼
Spring Cloud Config Server
(encrypt.key)
     │
Decrypts Values
     │
     ▼
Microservices
Receive Plain Text Configuration
```

---

###### Summary

`encrypt.key` is used by Spring Cloud Config Server to encrypt and decrypt sensitive configuration values such as database passwords, API keys, and secrets. It allows secure storage of configuration in Git while transparently providing decrypted values to client applications at runtime.

###### Cloud Server exposes 2 endpoints:
- Encrypt:
  - [Encrypt Text](http://localhost:888/encrypt) accepts text content and returns encrypted content:
  ![EncryptExample](images/EncryptExample.png)
- Decrypt:
  - [Decrypt Text](http://localhost:888/decrypt) accepts encrypted content and returns decrypted content:
  ![DecryptExample](images/DecryptExample.png)
- For encryption and decryption, encrypt.key will be used.

##### Keystore & RSA Key Pair:
- Instead of storing a plain-text encryption key directly in application.yml, Spring Cloud Config can use a Java Keystore.

- A Keystore is a secure file that stores encryption keys and certificates.

- Using the Java keytool utility, an RSA key pair (public key and private key) can be generated and stored inside the keystore.

- Spring Cloud Config Server uses the key pair from the keystore to:
  - Encrypt sensitive data (e.g., database passwords)
  - Decrypt encrypted values before sending configuration to clients

- ##### Benefits:
  - More secure than storing encryption keys in plain text
  - Easier key management
  - Suitable for production environments
  - Supports asymmetric encryption using RSA key pairs
- ##### How Encryption and Decryption Work with a Keystore

  - ###### Encryption

    - When a sensitive property (e.g., a database password) needs to be secured, Spring Cloud Config Server uses the **public key** from the RSA key pair stored in the keystore to encrypt the value.

      ```text
      Plain Text Password
              |
              v
      Public Key
              |
              v
      Encrypted Value ({cipher}...)
      ```

    - The encrypted value can then be safely stored in the Git configuration repository.

  - ###### Decryption

    - When a client application requests configuration from the Config Server, the server uses the **private key** from the keystore to decrypt the encrypted property before returning it.
      ```text
      Encrypted Value ({cipher}...)
                |
                v
            Private Key
                |
                v
          Plain Text Value
      ```

    - The client receives the actual value and does not need to perform any decryption.
  - ###### Why Use a Key Pair?
    - Public key is used for encryption.
    - Private key is used for decryption.
    - Private key remains securely stored in the Config Server.
    - Sensitive values can be safely stored in Git repositories.
    - More secure than storing a plain text encryption key.

  - ###### Flow
    ```text
    Client Secret
        |
        v
    Encrypt (Public Key)
        |
        v
    Store in Git Repository
        |
        v
    Config Server Reads Value
        |
        v
    Decrypt (Private Key)
        |
        v
    Send Plain Text to Client Application
    ```
- ##### How RSA is different from AES ?
  - AES is like having **one master key** for a safe, while **RSA** is like having a **lock and key pair** where
    - one key is public [which can be exposed/shared to public] (to lock i.e encryption) and
    - another key is private [we will keep it with us secretly] (to unlock i.e decryption) and
- ##### What is Keytool and why use it ?
  - Keytool is a command-line utility provided with JDK.
  - It helps you create and manage:
    - keystores
    - generate key pairs (eg: RSA Keys) and
    - handle digital certificates.
- ##### Command to create Keystore is:
  - Check ```where java``` and we get
    ```powershell
    C:\Users\mveerano>where java
    C:\Program Files\Eclipse Adoptium\jdk-18.0.2.101-hotspot\bin\java.exe
    C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\java.exe
    ```
  - Go to bin and we find **keytool.exe** and open cmd over there and run below command
    ```powershell
    keytool -genkeypair -alias config-server-key -keyalg RSA -dname "CN=Config Server,OU=Spring Cloud,O=Company" -keypass govirosh -keystore C://certi/certiconfig-server.jks -storepass govirosh
    ```
  - In Short
    ```text
    -keystore  = Password for the .jks file
    -storepass = Unlocks the keystore
    -alias     = Name of the key inside the keystore
    -keypass   = Password for the private key
    -keyalg    = RSA algorithm
    -dname     = Certificate owner details
    ```
    - Think of it like:
      ```text
      Locker (Keystore File)
        Password → storepass
      ------------------------------------------------------
      Inside Locker:
        Key Named → alias
        Protected By → keypass
      ```

  - Detailed Explanation of each param: 
    - **-genkeypair**:
      - Generates a new public/private key pair.
      - Public Key  → Used for Encryption
      - Private Key → Used for Decryption
      - Spring Cloud Config uses this key pair to encrypt and decrypt secrets.
    - **-alias config-server-key**
      - A unique name that identifies the key entry inside the keystore like below
        ```text
        Keystore (config-server.jks)
          └── config-server-key
        ```
      - And Spring Boot configuration:
        ```yaml
        encrypt:
          key-store:
            alias: config-server-key
        ```
    - **-keyalg RSA**
      - Specifies the encryption algorithm.
      - RSA = Asymmetric Encryption [For Spring Cloud Config, RSA is typically used.]
      - Common alternatives:
        - DSA
        - EC
    - **-dname**
      - Distinguished Name (certificate owner information).      
        | Part | Meaning |
        | :--- | :--- |
        | CN | Common Name |
        | OU | Organizational Unit |
        | O | Organization |
        | L | Locality/City |
        | ST | State |
        | C | Country |
      - Our own command can be like ```-dname "CN=Config Server,OU=Microservices,O=GoMad,L=Bangalore,ST=Karnataka,C=IN"```
      - This information becomes part of the generated certificate.
    - **-keypass mypass**
      - Password protecting the private key.
        ```text
        Private Key Password = mypass
        ```
      - Spring Boot:
        ```yaml
        encrypt:
          key-store:
            secret: mypass
        ```
    - **-keystore config-server.jks**
      - Name of the Java Keystore file to create.
      - It creates **config-server.jks**.
      - This file contains:
        - Certificate
        - Public Key
        - Private Key
      - Place it in:
        - src/main/resources/ or
        - config/
    - **-storepass mypass**
      - Password protecting the keystore file itself.
        ```text
        Keystore Password = mypass
        ```
      - Springboot:
        ```yaml
        encrypt:
          key-store:
            password: mypass 
        ```
  - ##### Mapping to Spring Cloud Config
    - If created using:
      ```powershell
      keytool -genkeypair ^
      -alias config-server-key ^
      -keyalg RSA ^
      -keypass mypass ^
      -keystore config-server.jks ^
      -storepass mypass
      ```
    - then:
      ```yaml
      encrypt:
        key-store:
          location: classpath:config-server.jks
          password: mypass
          alias: config-server-key
          secret: mypass
      ```
    - Recommended Production Command:
      ```powershell
      keytool -genkeypair `
      -alias config-server-key `
      -keyalg RSA `
      -keysize 2048 `
      -validity 3650 `
      -dname "CN=Config Server,OU=Microservices,O=GoMad,L=Bangalore,ST=Karnataka,C=IN" `
      -keypass StrongKeyPassword123 `
      -keystore config-server.jks `
      -storepass StrongStorePassword123 
      ```
    - Additional options:
      - keysize 2048 → RSA key length (recommended minimum)
      - validity 3650 → certificate valid for 10 years
  - ###### Config setup:
    - No changes in client setup
    - In Cloud config Server side:
      - Copy C://certi/certiconfig-server.jks to src/main/resources of cloud config server folder.
    - application.yml will be like:
      ```yaml
      spring:
        application:
          name: configserver
        cloud:
          config:
            server:
              git:
                uri: https://github.com/govindmaddala2107/app-config
                #          If git repo is private add username and password
                username: ${GIT_USER}
                password: ${GIT_TOKEN}
      # encrypt:
      #   key: {ENCRYPT_KEY}

      encrypt:
        key-store:
          location: config-server.jks
          password: ${CONFIG_PASSWORD}
          alias: config-server-key
          secret: ${CONFIG_PASSWORD}
      logging:
        level:
          org.springframework.cloud.config.server: ERROR

      server:
        port: 888
      ```
  - Same Encrypt and Decrypt works same but now encrypted response will be a bigger string like:
    ```text
    AQCctP2LLrURp2WqovmUH6QdBN6jw5dKkvkent4YScF/wO73pu720TukJnOtqDYjll2tMn5yn35NL/bTEgMx/XMUzbKHt0mzxd2nr1L9mC2VvLwHTdLRz2W3bPBFxT2fycE0C+R8MHwX3oaMYd6tMOqZbNX7njHwUP0GDH9FvkCCyEihPj3yuQ3Qz3z0Q5061ODqnxfMBaJX2ETzRx0tBTzJesf9AdvqGiKlDnJjzkOLr4yCmGHk7igXfB4ehQ0Y+Xk0vmex54gUunLPUdaPENdpC+pB11HDaJyltp2FCBt1ZD7aCNaeYATv+ltGz2lWoELAQOI5VbsKml3u3K5UjJPvFW7LP9BWf8/kO/y97bE90ltbBaqOooF5b2QUhTAzuNs=
    ```
    - If you want to keep these ciphered values in db, keep them like 
      ```text
      {cipher}AQCctP2LLrURp2WqovmUH6QdBN6jw5dKkvkent4YScF/wO73pu720TukJnOtqDYjll2tMn5yn35NL/bTEgMx/XMUzbKHt0mzxd2nr1L9mC2VvLwHTdLRz2W3bPBFxT2fycE0C+R8MHwX3oaMYd6tMOqZbNX7njHwUP0GDH9FvkCCyEihPj3yuQ3Qz3z0Q5061ODqnxfMBaJX2ETzRx0tBTzJesf9AdvqGiKlDnJjzkOLr4yCmGHk7igXfB4ehQ0Y+Xk0vmex54gUunLPUdaPENdpC+pB11HDaJyltp2FCBt1ZD7aCNaeYATv+ltGz2lWoELAQOI5VbsKml3u3K5UjJPvFW7LP9BWf8/kO/y97bE90ltbBaqOooF5b2QUhTAzuNs=
      ```
