# General overview how to use the project

## How to run the cluster
### Prerequisites
You need to have Kubernetes installed before hand (Kubernetes is already included in minikube, so it is enough to just install it)
### Execution
Start minikube and Kubernetes dashboard. The dashboard is optional but gives a nice overview.
```
minikube start
minikube dashboard (optional but gives you an)
```

Start the cluster
```
cd <PROJECT_LOCATION>
cd deployments
kubectl apply -f tags.yml
```

Kubernetes will automaticaly download the containers from my docker repo. Instead of tags.yml, you can also use monitoring.yml which is the file that Irene with the basic monitoring. However, I have not tested it yet. 
Then, go to the dashboard -> Ingress. Use the endpoint provided there (for me it's http://192.168.49.2). Add /tag/ to run the webservice interface. The result should be something like http://192.168.49.2/tag/

## Code structure
The classifier code is the repo itself. The webapp code is located in the folder myweb of the general repo.
### Code structure for the model (classifier)
Here I will just quickly summarize the important python files.
1. src.training_classifier_mybag / src.training_classifier_tfidf - lauches a subroutine to train the model. The resutl is dump of the model in the data folder.
2. src.evaluation.evaluate_mybag / src.evaluation.evaluate_tfidf - evaluates the two models. The result is a print with the metrics.
3. src.serve_model_tftidf - used to create the /predict endpoint. The webapp posts requests to it and it replys with json which contains the prediction.

### Code structure of myweb
myweb is a Java Spring web app to provide the service. 
1. src.main.java.myweb.ctrl - This directory has 2 controlers
    1. HelloWorldController - for the entry (no-endpoint) - just displays hello world to the user
    2. TagController - for the /tag endpoint. It basically holds the whole logic of the app.
2. src.main.java.myweb.data.Tag - data object which just hold the fields that needs to be send to the predict endpoint 
3. src.main.java.myweb.MywebApplication - used to just start the app
4. src.main.resources - contains the ui of the app
    1. templates.tag.index.html - html for the look of the webapp
    2. static.tag.script.js - the logic behind the app functionality (which is just the button)
    3. static.tag.format.css - css for the size of the button, etc.

NOTE: when building the docker image for myweb, you need to have the jar of the app in the target folder (as specified in the Dockerfile). To build jar from java project you can use the command ```mvn clean package```.  


### WINDOWS EXTRA:
1. `docker build -t mlc . `
2. `cd myweb` then `mvn clean package`
3. `docker build -t myweb . `
4. `docker compose up` (do this in root folder not in myweb)