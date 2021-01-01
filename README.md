# exchange-rates-nicaragua

This app gets the buy and sell prices of US Dollar in Nicaraguan Cordobas from differents banks in Nicaragua such as: Atlántida, Avanz, BAC, BANPRO, BDF, FICOHSA and LAFISE

![alt text](https://github.com/adarali/exchange-rates-nicaragua/raw/master/screen01.jpeg)

To compile just install add maven to PATH and run the command:

`mvn clean package`

run the jar file exchange-rates-exec.jar in the "target" folder with java 1.8

`java -jar exchange-rates-exec.jar -s`

That command will create a server a server that listens on port 8080.
To change the port just specify the port number after -s

To view the app: go to http://localhost:8080/

To get the rates in JSON format: go to http://localhost:8080/rates
