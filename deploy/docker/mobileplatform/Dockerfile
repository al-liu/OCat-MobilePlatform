FROM java:8
VOLUME /tmp
ADD mobile-platform-1.0-SNAPSHOT.jar ocat-mp.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/ocat-mp.jar"]