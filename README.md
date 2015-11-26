# Getting started

### first of all, build common
`(cd common ; ./gradlew clean build)`

### start config server, will run on well known port 8888
`(cd cloud-config-server ; ./gradlew run)`

### start eureka server, will run on well known port 8761 including dashboard
`(cd eureka-server ; ./gradlew run)`

### start current-data-service which holds the single point of truth
`(cd current-data-service ; ./gradlew run)`

### start other services that form the distributed system and can be scaled to your needs
```
(cd aggregated-report-cache ; ./gradlew run)
(cd aggregator-service ; ./gradlew run)
(cd location-service ; ./gradlew run)
(cd historize-aggregated-report-job ; ./gradlew run)
```

### start up some robots to fill the system with data
locations are consistent with what our location service knows
```
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=MUC)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=FRA)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=BER)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=WIE)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=PAR)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=LON)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=HAV)
(cd ice-selling-robot ; ./gradlew run -Ddata.source.location=TIM)
```


### start reporting service, for convenience on well known port 10000
`(cd reporting-service ; ./gradlew run)`

### open dashboard and report
```
open http://localhost:8761/
open http://localhost:10000/
```