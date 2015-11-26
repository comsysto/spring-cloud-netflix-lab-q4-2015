This file can serve as a template for the resulting blog post.

# TODO give me a cool title

## Our Goal

- get to know Spring Cloud Netflix
- evaluate potential use in customer projects
- provoking potential problems in distributed systems. Our focus was not on the ubiquitous "a service can fail" scenario (see https://github.com/Netflix/SimianArmy/wiki/Chaos-Monkey), but rather on (temporarily) slow services due to heavy load.

## Our Use Case

- TODO tell the story (robots selling ice, we want current reports)
- TODO describe the structure
- TODO architecture diagram
- TODO screenshot from report
- TODO screenshot from cloud dashboard
- TODO screenshot from hystrix dashboard
- technologies from spring cloud: eureka, hystrix, config server, hystrix dashboard

## Understanding the Bottleneck

- the critical point of the entire system is the aggregation due to its slow connection
- remedy 1: scale out
- remedy 2: design for always using the cache
- TODO statistics / tables

## Our Lessons Learned

- Spring Boot makes it really easy to run dozens of services.
- Spring Boot makes it really hard to figure out what is wrong when things do not work out of the box. Available Spring Cloud documentation is not always sufficient.
- Eureka works like a charm when it comes to service discovery. In another lab on distributed systems, we spent a lot of time working around this issue (TODO link to hash-collision posts). This time, everything was just right.

## In Brief: Should you use Spring Cloud Netflix?

- Eureka or some sort of "intelligent" service discovery: definitely yes when aiming at a microservice architecture
- Hystrix: very useful to preventing cascading errors throughout the system, but cannot be used in a production environment without suitable monitoring. Also, it introduces a few pitfalls in development (such as changed behavior due to timeouts while debugging in a breakpoint).
- we did not evaluate integration issues with non-Spring-Boot projects. For Spring Boot, almost everything is pretty much up and running with a single dependency. However, Spring Boot might be too simplistic for complex services (see Lessons Learned).