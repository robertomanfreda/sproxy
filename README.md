# Sproxy
![Java CI with Maven](https://github.com/robertomanfreda/sproxy/workflows/Java%20CI%20with%20Maven/badge.svg)

#### A Proxy developed using Spring Boot, Java and docker  

---
###### What is Sproxy

Sproxy is a real proxy, it captures the requests (all supported types by the Spring framework) and forwards them to the
requested url, applying different types of modifications:  

HTTP METHOD     | IMPLEMENTATION STATE
--------------- | ---------------
GET             | implemented
HEAD            | implemented
POST            | implemented
DELETE          | not implemented
OPTIONS         | implemented
PATCH           | implemented
PUT             | implemented
TRACE           | not implemented for security reasons   

---
###### Sproxy as transparent proxy

It is possible to specify the protocol, for instance specifying HTTP   
`http://localhost:8080/http://postman-echo.com/get?foo1=bar1`  

Or HTTPS  
`http://localhost:8080/https://postman-echo.com/get?foo1=bar1`  

When there is no protocol Sproxy will try to forward the request, to the requested URL, using HTTPS first;  
If the operation will fail Sproxy will use the HTTP   
`http://localhost:8080/postman-echo.com/get?foo1=bar1`  

In the same way is possible using directly an IP address  
`localhost:8080/52.73.240.226/get?foo1=bar1`  
or  
`localhost:8080/https://52.73.240.226/get?foo1=bar1`  

The port can also be specified directly in the request  
`http://localhost:8080/postman-echo.com:80/get?foo1=bar1`  
`http://localhost:8080/postman-echo.com:443/get?foo1=bar1`    

The url can be customized in a lot of different ways, Sproxy will try to understand where
to send the request and which parameters should be used.
 
---
###### Sproxy features  
Sproxy provides different features to enable some extras.

Available features:
- FEATURE TLS
  - With this feature you will be able to set up TLS. Sproxy starts on port 8080 by default but enabling TLS it will
    start on port 8443.  
    You can also set up the http to https redirection, just assigning `true` to the `http-to-https` property in the
    configuration file.  
    Supported keystore (how to create a keystore is out of scope but search on google, you will find how to do it) types 
    are:   
      - PKCS12
      - JKS

---
###### Sproxy mods
With Sproxy is also possible using "mods".

Available mods:   
- MOD HEADERS   
    - Thanks to this mod it is possible to modify both request and response headers.
- MOD URL  
    - This mod permits to define a whitelist and a blacklist to grant or deny the destination urls that Sproxy will be 
      able to reach.
- MOD SECURITY  
    - This mod is still not available... interesting features about session management and authentication will be added.
- MOD WAF  
    - Through this mode Sproxy is able to enable the Spring's built-in StrictHttpFirewall.  

---
###### Why should I use Sproxy?
Because Sproxy provides several levels of security and conveniences!  
You could use it as a transparent proxy but enabling extra features.  
It can be extremely useful when you need to resolve CORS related issues or more generally when you need to modify both
request or response headers.  
If you need to enable https for your endpoints you could simply enable https on Sproxy and he will take care of 
transporting traffic to others services in http reporting the responses to the client using https.  
It's useful when you need to restrict the access for specific urls, you can do it by using the MOD URL, mixing 
white-listing and black-listing.  
Improve security making Sproxy acts as a Web Application Firewall, it uses the powerful built-in Spring Boot 
StrictHttpFirewall.  
Others interesting features like session management and users authentication are coming, it will be possible to assign
users credentials in order to restrict the access using both Bearer and Basic authentication.  

---
###### Sproxy and docker
Project Sproxy is strictly connected to docker in order to retrace the java WORA (Write Once Run Anywhere) concept 
with extreme simplicity and portability. So if you want to run it out of the docker context (it's possible too) you are 
free to do it but you'll not find any useful information about configuration or other stuff here.

Here's a minimal docker compose file (is everything you need) useful to run Sproxy:   
**docker-compose.yaml**  
```yaml
version: "3.8"

services:
  sproxy:
    container_name: sproxy
    image: robertoman/sproxy:latest
    ports:
      - 80:8080   #(if FEATURE TLS is disabled) or (if FEATURE TLS is enabled && http-to-https == true)
      - 443:8443  #(if FEATURE TLS is enabled)
    volumes:
      - ./config:/config
      - ./logs:/var/log/sproxy
    environment:
      - spring.profiles.active=default,eureka #(if eureka is enabled)
```  

Here's the recommended **directory structure**  
```
- sproxy
     |
     +file----> docker-compose.yaml
     |
     +folder--> config
     |            |
     |            +file--> application.yaml
     |            |
     |            +file--> application-eureka.yaml  #(if eureka is enabled)
     |            |
     |            +file--> keystore.p12             #(if FEATURE TLS is enabled)
     |
     +folder--> logs
                  |
                  +file--> sproxy.log               #(autogenerated at startup)
```

So after this you can simply run `docker-compose up -d` (at the same level of docker-compose.yaml) to start Sproxy.

___
###### Modify default Sproxy properties
Sproxy comes with a default properties file, shipped within the jar. If you want to inspect the default properties you
should give a look at the `application.yaml` here: 
https://github.com/robertomanfreda/sproxy/blob/master/src/main/resources/application.yaml  

Anyway it is not recommended using the default file, you should provide an external configuration to customize the
sproxy mods and all other features like TLS and so on. Create the file and place it in the `/config` folder 
(explained below).

Accepted names for the configuration file are:  
- application.yaml
- application.yml
- application.properties

Eventually additional configuration files can be add, but if you do that you need to explicitly define spring active
profiles through the docker-compose.

Here's a complete example of configuration file:  
**application-sproxy.yaml**
```yaml
logging:
  level:
    com.robertoman.sproxy: debug

config:
  show-homepage: true

  feature:
    #---------- FEATURE TLS ----------#
    tls:
      enabled: true
      http-to-https: true
      key-alias: alias
      key-store-password: password
      key-store: '/config/keystore.p12'
      key-store-type: PKCS12
  
  mod:
    #---------- MOD HEADERS ----------#
    headers:
      enabled: true
      request:
        allow-overrides: true
        map:
          # WARNING: the indentation is important, here's we are declaring a Map<String, List<String>>
          (https?://)?postman-echo.com/.*+:
                                          - 'Foo: bar'
                                          - 'Bat: baz'
                                          - 'Another-Custom-Header: custom'
          (https?://)?(www.)?google.(com|it)(/.*)?:
                                                  - 'An-Header: 1'
                                                  - 'Another-Header: 2'
      response:
        allow-overrides: false
        # WARING: just use '{}' if you don't have any value to specify or the startup will fail
        map: {}

    #---------- MOD URL ----------#
    url:
      enabled: true
      whitelist:
        - '(https?://)?postman-echo.com/.*+'
        - '(https?://)?yoursite.org((/)?.*+)?'
      blacklist:
        - '(https?://)?www.google.com'
        - '(https?://)?(www.)?evilsite.(com|org|net)((/)?.*+)?'

    #---------- MOD WAF ----------#
    waf:
      enabled: true
      allows:
        backslash: false
        url-encoded-double-slash: true
        url-uncoded-percent: false
        url-encoded-period: false
        url-encoded-slash: false
        semicolon: false
        host-names:
          - localhost
          - example.com
        http-methods:
          - GET
          - HEAD
          - POST
```

---
###### Sproxy Cloud - Eureka
Sproxy supports eureka registration, all dependencies are provided within the jar package.   
By default the autodiscovery is disabled.  
    
If you need to enable the discovery client features there are 2 steps to do:  
  - Create a file named `application-eureka.yaml` and place it into your config folder. 
  - Put in the environment this variable via docker-compose: `spring.profiles.active=default,eureka`
  
Here's an example of **application-eureka.yaml**:  
```yaml
spring:
  application:
    name: sproxy

config:
  eureka-server:
    proto: http
    base: localhost
    port: 7000

eureka:
  client:
    enabled: true
    service-url:
      defaultZone: ${config.eureka-server.proto}://${config.eureka-server.base}:${config.eureka-server.port}/eureka
  instance:
    hostname: ${spring.application.name}

management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
```

If you don't want to provide any external properties file you can compile Sproxy from sources:
- `git clone https://github.com/robertomanfreda/sproxy.git`
- `cd sproxy`
- modify what you want
- `mvn -U clean package`
- `docker build -t com.robertoman/sproxy:custom .`
- `docker run -d --name sproxy com.robertoman/sproxy:custom` mapping ports, config folder 
   and logs volumes as you prefer 

---
###### Testing Sproxy

More content will be added

---