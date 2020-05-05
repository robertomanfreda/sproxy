# Sproxy
![Java CI with Maven](https://github.com/robertomanfreda/sproxy/workflows/Java%20CI%20with%20Maven/badge.svg)

#### A Proxy developed using the Spring Framework and Java  

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
    - This mod is in an experimental state.  

---
###### TLS
Working  on it

---
###### Modify default Sproxy properties
Sproxy comes with a default properties file, shipped within the jar. If you want to inspect the default properties you
should give a look at the `application.yaml` here: 
https://github.com/robertomanfreda/sproxy/blob/master/src/main/resources/application.yaml  

Anyway it is not recommended using the default file, you should provide an external configuration to customize the
sproxy mods and all other features like TLS and so on.  

Accepted names for the file are:  
- application.yaml
- application.yml
- application.properties

Here's a complete example of configuration file:
```yaml
logging:
  level:
    root: info
    com.robertoman.sproxy: debug

config:
  #############################
  ############ TLS ############  -> THIS FEATURE IS STILL NOT AVAILABLE
  #############################
  tls:
    enabled: false


  #####################################
  ############ SProxy MODS ############
  #####################################
  mod:
    ########################
    ## SProxy mod HEADERS ##
    ########################
    # From here it is possible to modify both request and response headers (regex based)
    headers:
      enabled: true
      request:
        allow-overrides: false
        map:
          # Here's a bug regarding the loading of an empty map, DON'T REMOVE THIS LINE, will be fixed
          # Values here are: Map<String, List<String>>. Include your urls like this -> '[REGEX]'
          '[(https?://)?www.google.com/.*+]':
                                              - 'Bar: baz'
      response:
        allow-overrides: true
        map:
          # Here's a bug regarding the loading of an empty map, DON'T REMOVE THIS LINE, will be fixed
          # Values here are: Map<String, List<String>>. Include your urls like this -> '[REGEX]'
          '[(https?://)?postman-echo.com/.*+]':
                                              - 'Access-Control-Allow-Origin: *'
                                              - 'Foo: bar'  

    #########################
    ## SProxy mod SECURITY ##  -> THIS MOD IS STILL NOT AVAILABLE
    #########################
    # If this mod is enabled you need to login calling the /login endpoint in order to get a SProxy-Bearer
    # to use for Bearer Authentication
    security:
      enabled: false
      username: username
      password: password
      methods: "POST,HEAD,GET"
      keystore:
        zip:
          path: /home/roberto/keystore/keystore.zip

    ####################
    ## SProxy mod URL ##
    ####################
    # From here it is possible to write a whitelist url that SProxy will be authorized to call (regex based)
    url:
      enabled: true
      whitelist:
        - '(https?://)?postman-echo.com/.*+'
      blacklist:
        - '(https?://)?www.google.com'

    ####################
    ## SProxy mod WAF ##  -> THIS MOD IS IN AN EXPERIMENTAL STATE
    ####################
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
```

---
###### Sproxy and docker
Project Sproxy is strictly connected to docker, all the development process was executed basing on it. So if you want to
run it out of the docker context you are free to do it but you'll not find any information about configuration or other
stuff here.

Here's a minimal docker compose file (is everything you need) useful to run Sproxy:  
 
**docker-compose.yaml**  
```yaml
version: "3.7"

services:
  sproxy:
    container_name: sproxy
    image: robertoman/sproxy:latest
    ports:
      - 6380:6380
    volumes:
      - ./config:/config
      - ./logs:/var/log/sproxy
```  

Here's the recommended **directory structure**  
```
- sproxy
     |
     +--- docker-compose.yaml
     |
     +--- config
     |       |
     |       +--- application.yaml
     |
     +--- logs
            |
            +--- sproxy.log (autogenerated at startup)
```

So after this you can simply run `docker-compose up -d` (at the same level of docker-compose.yaml) to start Sproxy.

Or if you prefer you can use it without docker compose but directly using docker:   
Pull the docker image from https://hub.docker.com/r/robertoman/sproxy and run it
- `docker pull robertoman/sproxy:latest`  
- `docker run -d --name sproxy -p 8080:8080 com.robertoman/sproxy:latest` taking care of mapping config and logs volumes


If you don't want to provide an externalized properties file you can compile Sproxy from sources:
- `git clone https://github.com/robertomanfreda/sproxy.git`
- `cd sproxy`
- modify the `application.yaml` at src/main/resources/application.yaml as you prefer
- `mvn -U clean package; docker build -t robertoman/sproxy:custom .`
- `docker run -d --name sproxy -p 6380:6380 com.robertoman/sproxy:custom` taking care of mapping config and logs volumes