# Sproxy
#### A Proxy developed using the Spring Framework and Java

This project demonstrates the power of the [Spring Framework](https://pivotal.io/spring-app-framework) and how
immediate it can be to manage complex infrastructures with the help of [Docker](https://www.docker.com/).  

---
###### What is Sproxy

Sproxy is a real proxy, it captures the requests supported by Spring:  

 - GET
 - HEAD
 - POST
 - DELETE  
 - OPTIONS
 - PATCH
 - PUT
 - TRACE
   
and forwards them to the requested url (current implementation only supports HEAD and GET).

---
###### Sproxy features

It is possible to specify the protocol, for instance specifying HTTPS   
`http://localhost:8080/http://postman-echo.com/get?foo1=bar1`  

Or HTTP  
`http://localhost:8080/https://postman-echo.com/get?foo1=bar1`  

When no protocol is specified Sproxy will try to proxy the request, to the requested URL, using HTTPS first;  
If the operation will fail Sproxy will switch the protocol trying with HTTP   
`http://localhost:8080/postman-echo.com/get?foo1=bar1`  

In the same way is possible using directly an IP address  
`localhost:8080/52.73.240.226/get?foo1=bar1`

The port can also be specified directly in the request  
`http://localhost:8080/postman-echo.com:80/get?foo1=bar1`  
`http://localhost:8080/postman-echo.com:443/get?foo1=bar1`

---
###### I want to try Sproxy  
Just 4 commands  
 - `git clone https://github.com/robertomanfreda/sproxy.git`
 - `cd sproxy`
 - `mvn clean package docker:build`
 - `docker run -d --name sproxy -p 8080:8080 com.robertomanfreda/sproxy:latest`
