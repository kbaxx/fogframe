FROM hypriot/rpi-java

RUN sudo apt-get update
RUN sudo apt-get install build-essential git ant

RUN git config --global http.sslVerify false
RUN git clone https://github.com/jnr/jffi.git

RUN ant jar -f jffi/build.xml
RUN sudo cp jffi/build/jni/libjffi-1.2.so /usr/lib

ADD fogcontrolnode-0.0.1-SNAPSHOT.jar .
ADD main.properties .

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "fogcontrolnode-0.0.1-SNAPSHOT.jar"]
