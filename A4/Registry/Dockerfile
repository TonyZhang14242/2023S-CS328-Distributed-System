FROM tonyzhang14242/mpienv:latest
COPY *.jar /app.jar
CMD ["--server.port=9000"]
EXPOSE 9000
ENTRYPOINT ["java","-jar","/app.jar"]