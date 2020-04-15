# SProxy
![Java CI with Maven](https://github.com/robertomanfreda/sproxy/workflows/Java%20CI%20with%20Maven/badge.svg)

#### A Proxy developed using the Spring Framework and Java  

---
 
###### What is SProxy

SProxy is a real proxy, it captures the requests (supported by the Spring framework) and forwards them to the requested url:  

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
###### SProxy features

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
###### Sproxy profiles
SProxy provides different profiles for different proxying strategies:  
- tunneling-proxy  
  When this profile is active SProxy simply acts as a gateway, he turns the requests to the specified url without 
  making any changes to the request.  
  In this mode you can enable the SProxy security.  
  With enabled security any client needs to be authenticated,
  so he need to call the `/login` endpoint before to be able to use all other features.  
  With disabled security any client can use all SProxy's endpoints without restrictions.


---
###### I want to try SProxy
SProxy provides different deploy methods:  

1) Pull the docker image from https://hub.docker.com/r/robertoman/sproxy:    
- `docker pull robertoman/sproxy:latest`  
- `docker run -d --name sproxy -p 8080:8080 com.robertoman/sproxy:latest`  
  
2) Manually Compile the image   
- `git clone https://github.com/robertomanfreda/sproxy.git`
- `cd sproxy`
  - `mvn clean package docker:build` or `docker build -t robertoman/sproxy:latest .`  
- `docker run -d --name sproxy -p 8080:8080 com.robertoman/sproxy:latest`


---
###### Testing SProxy
SProxy comes up with a useful postman collection that grants continuous testing and helps the developer to test every
single REST endpoint during the development phase.    
The collection can be run using the Postman runner.  
Here's the public access: https://documenter.getpostman.com/view/5504064/SzYdSGSb