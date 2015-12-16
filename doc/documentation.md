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

Our business case is about an ice selling company, which is acting on worldwide locations. On each location there are ice selling robots. At the company's headquarter we want to show an aggregated report about the ice selling activities for each country.


All our components are implemented as dedicated microservices using Spring Boot and Spring Cloud Netflix. Service discorvery is implemented using Eureka server. The communication between the microservices is RESTful.

- TODO architecture diagram

There is a basic location-service, which knows about all locations provided with ice-selling-robots. The data from all these locations have to be part of the report.

For every location there is one microservice representing an ice-selling-robot. Every ice-selling-robot has locally stored information about the amount of totally sold ice cream and the remaining stock amount. Each of them continuously pushes these data to the central current-data-service. It fails with a certain rate, which is configured by a central Config Server.

The current-data-service stores this information locally. Every time he receives an update from one of the ice-selling-robots, he takes the new value and forgets about the old one. Old values are also forgotten if their timestamp is too old.

The current-data-service offers an interface by which the current value for the totally sold amount of ice cream or the remaining stock amount can be retretrieved for one location. This interface is used by an aggregator-service, which is able to generate and deliver an aggregated report on demand. For all locations provided by the location-service the current data is retrieved from the current-data-service, which is then aggregated by summing up the single values from the locations grouped by the locations country. The delivered report consists of the summed up values per country and data type (totally sold ice cream and remaining stock value).

Because the connection between aggregator-service and current-data-service is quite slow, the calculation of the report takes a lot of time (we simply simulated this slow connection with a wifi connection, wich is slow compared to an internal service call on the same machine). Therefore an aggregated report cache has been implemented as fallback. Switching to this fallback has been implemented using Hystrix. At fix intervals the cache is provided with the most actual report by an own historizing job. 

The reporting service is the only service containing a user interface. It generates some kind of simple html-based dashboard, which can be used by the business section of our company to get an overview of all the different locations. The data presented to the user is retrieved from the aggregator-service. Because this service is expected to be slow and prone to failure, a fallback is implemented which retrieves the last report from the aggregated-report-cache.

- TODO screenshot from report

Eureka comes with a built-in dashboard, showing all registrated services:

- TODO screenshot from cloud dashboard

The circuit-breaker within the aggregator-service can be monitored from Hystrix dashboard.

- TODO screenshot from hystrix dashboard

## Understanding the Bottleneck
- In addition to delaying the execution time of the aggragator-service we also allocated a thread pool of limited size for the getReport call itself. 
- As a result, the number of concurrent (or "parallel") calls to the report-service is limited (roughly) to the size of the thread pool.

- This way we can easily max out the capacity for on-demand generated reports, forcing the system to the fallback of the cached report.

The relevant part of the reporting-services declaration looks as depicted in the following code snippet. The primary call getReport() is annotated with @HystrixCommand and configured to use the cached report as fallbackMethod  : 

```
@HystrixCommand(
    fallbackMethod="getCachedReport",
    threadPoolKey="getReportPool"
)
public Report getReport() {
    return restTemplate.getForObject("http://aggregator-service/", Report.class);
}

public Report getCachedReport() {
    return restTemplate.getForObject("http://aggregated-report-cache/", Report.class);
}	
```

From the user perspective it's fairly easy to tell wich of both methods in question has actually been used:  
Every served report contains a field wich denotes the age of the report. This value simply is put together as the time delta between the creation of a report and the time this report has been served.  
As soon as the reporing-service delegates incoming requests to the fallback method, the age of the served report starts to increase.
 
## Testing 

With our bottleneck set up it should be fairly easy to force the system into calling the fallback method. 

We decide to do so with jmeter. Therefore we configure requests to the reporting-service. 
During multiple following test runs we adjust the amount of our simulated parallel users to trigger the expected internal behaviour while keeping an eye on the observable responses.  

Our main setup includes:  
Measurement with 1 aggregation server, 1min test duration, 500ms Hit-Rate per Thread, Historize-Job-Rate 30s, thread pool size: 5

We conduct test runs with a jmeter thread pool size (=number of concurrent simulated users) of 3, 5 and 7.
 
Although we were quite curious, the results pretty much reflect our exceptions. 
When using a jmeter thread count below the size of the service thread pool, the servics calls result in 100% success.
Setting sizes of both pools equal already gives a mall noticeable error rate. And setting the size higher than the thread pool results in growing failures and fallbacks, also forcing the circuit breaker into short circuit state.
Our measured results a noted in the following table: 

TABLE: number of concurrent jmeter threads and the resulting average report age 
- 3 threads: 0,78s average age
- 5 threads: 1,08s average age
- 7 threads: 3,05s average age

additionally, after gaining these results, we changed the setup in a way that eliminates the slow connection. We did this by moving the current data service to the same machine as the aggregation-service.
Thus the slow connection has now been replaced with an internal, fast connection. 
With this new setup we conduct a final test run, gaining the following result:
  
- 7 threads, fast network: 0,74s average age
By eliminating one part of our bottleneck, the value of report age significantly drops to somewhere close below the first test run.  

- the critical point of the entire system is the aggregation due to its slow connection
- remedy 1: scale out (hard to test on our computer which already run so many threads)
- remedy 2: optimize slow connection (see measurement)
- TODO statistics / tables
- remedy 3: design for always using the cache (only applicable when results are identical for all users. could be done here, but only because we have a simplistic scenario.)
- note that the job filling the cache can also fail under heavy load!

## Our Lessons Learned

- Spring Boot makes it really easy to build and run dozens of services, but really hard to figure out what is wrong when things do not work out of the box. Available Spring Cloud documentation is not always sufficient.
- Eureka works like a charm when it comes to service discovery. Simply use the name of the target in an URL and put it into a RestTemplate.
- Everything else is handled transparently - including client-side load balancing with Ribbon (https://github.com/Netflix/ribbon)!
- In another lab on distributed systems, we spent a lot of time working around this issue. This time, everything was just right.
- Measuring the effect of scaling out is nearly impossible on a developer machine. This could be easily achieved in the cloud.

## In Brief: Should you use Spring Cloud Netflix?

- So what is our recommendation after all?
- We were totally impressed by the way Eureka makes service discovery as easy as it can be.
- Given you are running Spring Boot, starting the Eureka server and making each microservice a Eureka client is nothing more than dependencies and annotations. On the other hand, we did not evaluate its integration in other environments.
- Hystrix is very useful for preventing cascading errors throughout the system, but it cannot be used in a production environment without suitable monitoring unless you have a soft spot for flying blind.
- Also, it introduces a few pitfalls during development. For example, when debugging a Hystrix command the calling code will probably detect a timeout in the meantime which can give you completely different behavior.
- However, if you got the tools and skills to handle the additional complexity, Hystrix is definitely a winner.
- In fact, this restriction applies to microservice architectures in general. You have to go a long way for being able to run it - but once you are, you can scale almost infinitely.
