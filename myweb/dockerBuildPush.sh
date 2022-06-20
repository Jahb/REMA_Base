mvn clean package
docker build -t aimarinov/remla_myweb .
docker push aimarinov/remla_myweb
cd ..
cd deployments
kubectl apply -f monitoring.yml