# scheduler-micro-rest

## How to run

* mvn clean install
* mvn spring-boot:run
* Spring boot server zal starten op poort 3232, kan je gewoon aan via localhost:3232
* Sampler aanzetten in de Controller kan random jobs lanceren die elke seconde draaien om activiteit te simuleren
* Voorlopig staat er in JobViaREST nog een expliciete referentie naar localhost PC
 
## URLS
 
* http://localhost:3232/status
```
{"runningSince":"02-05-2018 17:38:27","lastJobExecuted":"02-05-2018 17:38:45","threadPoolSize":10,"jobsRunning":0,"jobsExecutedSoFar":19,"state":"STARTED"}
```

* http://localhost:3232/jobs
```
[{"lastRun":"02-05-2018 17:53:37","nextRun":"02-05-2018 17:53:38","name":"testJob","group":"testGroup"}, 
{"lastRun":"01-05-2018 10:10:37","nextRun":"05-05-2018 17:53:38","name":"testJob2","group":"testGroup"}]
```

* http://localhost:3232/add
Simpele UI om jobs toe te voegen.

* http://localhost:3232/edit/testJob
Simpele UI om jobs te editeren op basis van naam.

* http://localhost:3232/pause/testJob
Pauzeer jobs op basis van naam.

* http://localhost:3232/resume/testJob
Hervat jobs op basis van naam.

* http://localhost:3232/delete/testJob
Verwijder jobs op basis van naam.

## TODO

* Persistence koppeling
* Foutverwerking indien geen DB, indien geen vertaalservice