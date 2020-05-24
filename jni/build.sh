JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# The following shoud be run to generate the boilerplate CPP code
javac -h . *.java

rm *.so || true
rm *.class || true

# Compile & link CPP
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux Hello.cpp -o Hello.o
g++ -shared -fPIC -o libhello.so Hello.o -lc

# Compile Java
javac Hello.java

# Package JAR
jar cmf Hello.mf Hello.jar *.class libhello.so

# Execute JAR
# java -jar Hello.jar

# Start minikube
minikube start --driver=virtualbox

# Set docker env
eval $(minikube docker-env)

docker build -t hello:latest .
# docker run -p 8000:8000 hello:latest

# minikube delete

# Run in minikube
# kubectl run hello --image=hello:latest --image-pull-policy=Never

kubectl apply -f deploy.yaml
kubectl apply -f services.yaml
kubectl patch deployment hello-deployment -p "{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"date\":\"`date +'%s'`\"}}}}}"

# kubectl describe deployment hello-deployment

# Check that it's running
# kubectl get pods -o wide
# kubectl get pods -l app=hello
# kubectl get deployments hello-deployment
# kubectl describe deployments hello-deployment
# kubectl get replicasets
# kubectl describe replicasets
# kubectl describe services hello-service

minikube service hello-service --url

# This is to expose the service to the internet when running in GKE

# kubectl expose deployment hello-deployment --type=LoadBalancer --port 80 --target-port 8000