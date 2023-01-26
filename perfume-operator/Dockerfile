FROM openjdk:11-jre
USER root:root

# add new user njkol
RUN useradd -ms /bin/bash -u 5000 njkol

RUN mkdir /operator
COPY target/perfume-operator-0.0.1.jar /operator
WORKDIR /operator
CMD java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar perfume-operator-0.0.1.jar

# change user to njkol
USER njkol:njkol