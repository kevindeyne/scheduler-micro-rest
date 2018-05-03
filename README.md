# scheduler-micro-rest

## How to run

* mvn clean install
* mvn spring-boot:run
* Spring boot server zal starten op poort 80, kan je gewoon aan via localhost
* Random job zal elke seconde draaien om activiteit te simuleren
 
## URLS
 
* http://localhost/status
```
{"runningSince":"02-05-2018 17:38:27","lastJobExecuted":"02-05-2018 17:38:45","threadPoolSize":10,"jobsRunning":0,"jobsExecutedSoFar":19,"state":"STARTED"}
```

* http://localhost/jobs
```
[{"lastRun":"02-05-2018 17:53:37","nextRun":"02-05-2018 17:53:38","name":"testJob","group":"testGroup"}, 
{"lastRun":"01-05-2018 10:10:37","nextRun":"05-05-2018 17:53:38","name":"testJob2","group":"testGroup"}]
```