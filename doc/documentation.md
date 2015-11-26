This file can serve as a template for the resulting blog post.

# TODO give me a cool title

- In November 2015, we had the opportunity to spend three days with a greenfield project in order to get to know Spring Cloud Netflix.
- At comSysto, we always try to evaluate technologies before their potential use in customer projects to make sure we know their pros and cons.
- Of course, we had read about several aspects, but we never really got our hands dirty using it. This had to change!
- Besides coming up with a simple scenario that can be completed within a few days, our main focus was on understanding potential problems in distributed systems.
- First of all, any distributed system comes with the ubiquitous problem of failing services that should not break the entire application.
- This is most prominently addressed by Netflix' "Simian Army" (https://github.com/Netflix/SimianArmy/wiki) which intentionally breaks random parts of the production environment.
- However, we rather wanted to provoke problems arising under heavy load due to capacity limitations.
- Therefore, we intentionally designed a distributed application with a bottleneck that turned into an actual problem with many simultaneous requests.

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
- remedy 1: scale out (JMeter measurement 1)
- remedy 2: optimize slow connection (JMeter measurement 2)
- TODO statistics / tables
- remedy 3: design for always using the cache (only applicable when results are identical for all users. could be done here, but only because we have a simplistic scenario.)

## Our Lessons Learned

- Spring Boot makes it really easy to build and run dozens of services, but really hard to figure out what is wrong when things do not work out of the box. Available Spring Cloud documentation is not always sufficient.
- Eureka works like a charm when it comes to service discovery. Simply use the name of the target in an URL and put it into a RestTemplate.
- Everything else is handled transparently - including client-side load balancing with Ribbon (https://github.com/Netflix/ribbon)!
- In another lab on distributed systems, we spent a lot of time working around this issue. This time, everything was just right.

## In Brief: Should you use Spring Cloud Netflix?

- So what is our recommendation after all?
- We were totally impressed by the way Eureka makes service discovery as easy as it can be.
- Given you are running Spring Boot, starting the Eureka server and making each microservice a Eureka client is nothing more than dependencies and annotations. On the other hand, we did not evaluate its integration in other environments.
- Hystrix is very useful for preventing cascading errors throughout the system, but it cannot be used in a production environment without suitable monitoring unless you have a soft spot for flying blind.
- Also, it introduces a few pitfalls during development. For example, when debugging a Hystrix command the calling code will probably detect a timeout in the meantime which can give you completely different behavior.
- However, if you got the tools and skills to handle the additional complexity, Hystrix is definitely a winner.
- In fact, this restriction applies to microservice architectures in general. You have to go a long way for being able to run it - but once you are, you can scale almost infinitely.