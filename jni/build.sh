# Create the header with javac -h . ClassName.java

JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# The following shoud be run to generate the boilerplate CPP code
# javac -h . Hello.java

rm *.so || true
rm *.class || true

# Compile & link CPP
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux Hello.cpp -o Hello.o
g++ -shared -fPIC -o libhello.so Hello.o -lc

# Compile Java
javac Hello.java

# Package JAR
jar cmf Hello.mf Hello.jar Hello.class libhello.so

# Execute JAR
java -jar Hello.jar