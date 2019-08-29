FROM java:8-jdk
ADD target/sse-0.0.1-SNAPSHOT.jar sse.jar
COPY execute.sh execute.sh
RUN chmod +x execute.sh
#CMD ["./execute.sh","2.txt"]