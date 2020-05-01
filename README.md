# Sproxy
![Java CI with Maven](https://github.com/robertomanfreda/sproxy/workflows/Java%20CI%20with%20Maven/badge.svg)

#### A Proxy developed using the Spring Framework and Java  

---
 
###### What is Sproxy

Sproxy is a real proxy, it captures the requests (supported by the Spring framework) and forwards them to the requested url:  

HTTP METHOD     | IMPLEMENTATION STATE
--------------- | ---------------
GET             | implemented
HEAD            | implemented
POST            | implemented
DELETE          | not implemented
OPTIONS         | not implemented
PATCH           | not implemented
PUT             | not implemented
TRACE           | not implemented   

---
###### Sproxy features

It is possible to specify the protocol, for instance specifying HTTP   
`http://localhost:8080/http://postman-echo.com/get?foo1=bar1`  

Or HTTPS  
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
###### Sproxy mods
With Sproxy is also possible using "mods".

Available mods:   
- MOD HEADERS   
    - Thanks to this mod it is possible to modify both request and response headers.
- MOD URL  
    - This mod permits to define a whitelist and a blacklist to grant or deny the destination urls that Sproxy will be 
      able to reach.
- MOD SECURITY  
    - This mod is still not available.
- MOD WAF  
    - This mod is still not available.  

---
###### Modify default Sproxy properties
Working on it 

---
###### I want to try Sproxy
Sproxy provides different deploy methods:  

1) Pull the docker image from https://hub.docker.com/r/robertoman/sproxy:    
- `docker pull robertoman/sproxy:latest`  
- `docker run -d --name sproxy -p 8080:8080 com.robertoman/sproxy:latest`  
  
2) Compile from sources   
- `git clone https://github.com/robertomanfreda/sproxy.git`
- `cd sproxy`
  - `mvn clean package docker:build` or `docker build -t robertoman/sproxy:latest .`  
- `docker run -d --name sproxy -p 8080:8080 com.robertoman/sproxy:latest`