This file can serve as a template for the resulting blog post.

# Ice cream sales break microservices, Hystrix to the rescue

In November 2015, we had the opportunity to spend three days with a greenfield project in order to get to know Spring Cloud Netflix. At comSysto, we always try to evaluate technologies before their potential use in customer projects to make sure we know their pros and cons. Of course, we had read about several aspects, but we never really got our hands dirty using it. This had to change!

Besides coming up with a simple scenario that can be completed within a few days, our main focus was on understanding potential problems in distributed systems. First of all, any distributed system comes with the ubiquitous problem of failing services that should not break the entire application. This is most prominently addressed by Netflix' "Simian Army" (https://github.com/Netflix/SimianArmy/wiki) which intentionally breaks random parts of the production environment.

However, we rather wanted to provoke problems arising under heavy load due to capacity limitations. Therefore, we intentionally designed a distributed application with a bottleneck that turned into an actual problem with many simultaneous requests.

## Our Use Case

Our business case is about an ice selling company, which is acting on worldwide locations. On each location there are ice selling robots. At the company's headquarters we want to show an aggregated report about the ice selling activities for each country.


All our components are implemented as dedicated microservices using Spring Boot and Spring Cloud Netflix. Service discovery is implemented using Eureka server. The communication between the microservices is RESTful.

- TODO architecture diagram

There is a basic location-service, which knows about all locations provided with ice-selling-robots. The data from all these locations has to be part of the report.

For every location, there is one instance of the corresponding microservice representing an ice-selling-robot. Every ice-selling-robot has locally stored information about the amount of totally sold ice cream and the remaining stock amount. Each of them continuously pushes this data to the central current-data-service. It fails with a certain rate, which is configured by a central Config Server.

For the sake of simplicity, the current-data-service stores this information in-memory. Every time it receives an update from one of the ice-selling-robots, it takes the new value and forgets about the old one. Old values are also forgotten if their timestamp is too old.

The current-data-service offers an interface by which the current value for the totally sold amount of ice cream or the remaining stock amount can be retrieved for one location. This interface is used by an aggregator-service, which is able to generate and deliver an aggregated report on demand. For all locations provided by the location-service the current data is retrieved from the current-data-service, which is then aggregated by summing up the single values from the locations grouped by the locations' country. The resulting report consists of the summed up values per country and data type (totally sold ice cream and remaining stock value).

Because the connection between aggregator-service and current-data-service is quite slow, the calculation of the report takes a lot of time (we simply simulated this slow connection with a wifi connection, which is slow in comparison with an internal service call on the same machine). Therefore, an aggregated report cache has been implemented as fallback. Switching to this fallback has been implemented using Hystrix. At fixed intervals the cache is provided with the most current report by a simple scheduled job. 

The reporting service is the only service with a graphical user interface. It generates a very simplistic html-based dashboard, which can be used by the business section of our company to get an overview of all the different locations. The data presented to the user is retrieved from the aggregator-service. Because this service is expected to be slow and prone to failure, a fallback is implemented which retrieves the last report from the aggregated-report-cache. With this, the user can always request a report within an acceptable response time even though it might be slightly outdated. This is a typical example for maintaining maximum service quality in case of partial failure. 

- TODO screenshot from report

Eureka comes with a built-in dashboard, showing all registrated services:

- TODO screenshot from cloud dashboard

The circuit-breaker within the aggregator-service can be monitored from Hystrix dashboard.

- TODO screenshot from hystrix dashboard

## Understanding the Bottleneck
When using Hystrix, all connectors to external services typically have a thread pool of limited size to isolate system resources. As a result, the number of concurrent (or "parallel") calls from the aggregator-service to the report-service is limited by the size of the thread pool. This way we can easily overstress the capacity for on-demand generated reports, forcing the system to fall back to the cached report.

The relevant part of the reporting-service's internal declaration looks as depicted in the following code snippet (note the descriptive URLs that are resolved by Eureka). 
The primary method getReport() is annotated with @HystrixCommand and configured to use the cached report as fallbackMethod: 

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

In order to be able to distinguish primary and fallback calls from the end user's point of view, we decided to include a timestamp in every served report to indicate the delta between the creation and serving time of a report.  
Thus, as soon as the reporting-service delegates incoming requests to the fallback method, the age of the served report starts to increase.
 
## Testing 

With our bottleneck set up, testing and observing the runtime behaviour is fairly easy. 
Using jmeter we configured a testing scenario with simultaneous requests to the reporting-service. 
  
Basic data of our scenario:  
aggregation-server instances: 1, 
test duration: 60s, 
hit rate per thread: 500ms, 
Historize-Job-Rate: 30s, 
thread pool size for the getReport command: 5

Using the described setup we conducted different test runs with a jmeter thread pool size (=number of concurrent simulated users) of 3, 5 and 7.

Analyzing the served reports timestamps leads us to the following conclusion:  
 
Using a jmeter thread count below the size of the service thread pool results in a 100% success rate for the reporting-service calls.
Setting sizes of both pools equal already gives a small noticeable error rate. 
Finally, setting the size higher than the thread pool results in growing failures and fallbacks, also forcing the circuit breaker into short circuit states.
Our measured results are denoted in the following table: 

TABLE: number of concurrent jmeter threads and the resulting average report age 
- 3 threads: 0,78s average age
- 5 threads: 1,08s average age
- 7 threads: 3,05s average age

After gaining these results, we changed the setup in a way that eliminates the slow connection. 
We did so by deploying the current-data-service to the same machine as the aggregation-service.
Thus, the slow connection has now been removed and replaced with an internal, fast connection. 
With the new setup we conducted an additional test run, gaining the following result:
  
- 7 threads, fast network: 0,74s average age
By eliminating one part of our bottleneck, the value of report age significantly drops to a figure close below the first test run.

## Remedies

The critical point of the entire system is the aggregation due to its slow connection.
To address the issue, different measures can be taken. 
First, it is possible to scale out by adding additional service instances. Unfortunately, this was hard to test given the hardware at hand.
Second, another approach would be to optimize the slow connection, as seen in our additional measurements.
Last but not least, we could also design our application for always using the cache assuming that all users should see the same report. In our simplistic scenario this would work, but of course that is not what we wanted to analyze in the first place.

## Our Lessons Learned

Instead, let us explain a few take-aways based on our humble experience of building a simple example from scratch.

Spring Boot makes it really easy to build and run dozens of services, but really hard to figure out what is wrong when things do not work out of the box. Unfortunately, available Spring Cloud documentation is not always sufficient. Nevertheless, Eureka works like a charm when it comes to service discovery. Simply use the name of the target in an URL and put it into a RestTemplate. That's all! Everything else is handled transparently, including client-side load balancing with Ribbon (https://github.com/Netflix/ribbon)! In another lab on distributed systems, we spent a lot of time working around this issue. This time, everything was just right.

Furthermore, our poor deployment environment (3 MacBooks...) made serious performance analysis very hard. Measuring the effect of scaling out is nearly impossible on a developer machine due to its physical resource limitations. Having multiple instances of the same services doesn't give you anything if one of them already pushes the CPU to its limits. Luckily, there are almost infinite resources in the cloud nowadays which can be allocated in no time if required. It could be worth considering this option right away when working on microservice applications.

## In Brief: Should you use Spring Cloud Netflix?

So what is our recommendation after all?

First, we were totally impressed by the way Eureka makes service discovery as easy as it can be. Given you are running Spring Boot, starting the Eureka server and making each microservice a Eureka client is nothing more than dependencies and annotations. On the other hand, we did not evaluate its integration in other environments.

Second, Hystrix is very useful for preventing cascading errors throughout the system, but it cannot be used in a production environment without suitable monitoring unless you have a soft spot for flying blind. Also, it introduces a few pitfalls during development. For example, when debugging a Hystrix command the calling code will probably detect a timeout in the meantime which can give you completely different behavior. However, if you got the tools and skills to handle the additional complexity, Hystrix is definitely a winner.

In fact, this restriction applies to microservice architectures in general. You have to go a long way for being able to run it - but once you are, you can scale almost infinitely. Feel free to have a look at the code we produced at github (https://github.com/comsysto/spring-cloud-netflix-lab-q4-2015/) or discuss whatever you are up to at one of our user groups (https://comsysto.com/community.html#user-groups).
