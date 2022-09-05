# IOT_Data

This is consumer application  based on Java / Spring boot to receive "IoT events" from (Producer) a Kafka stream.  Consumer will store events on Cassandra data base and provide REST API to query events for clients.

### Prior Requirement
•	Java 8 +

•	Maven

•	Docker 

•	Postman / Any REST Client 

### Installation and run application
1. Pull repository for GitHub - https://github.com/fbagim/IOT_Data.git 
2. Set Kafka broker Bootstrap address on application. Properties file according to producer host Ip’s.

    > kafka.bootstrapAddress=localhost:9092,localhost:9094,localhost:9095
    
3. Execute Maven Build inside project
    > mvn clean install
4. Start Docker compose - 
    > docker-compose -f "docker-compose.yml" up --build -d   
    
      This will start Cassandra docker service and this will execute database queries to create cerate Data base / key space in Cassandra server-  iot_data.
      
    ![image](https://user-images.githubusercontent.com/7611920/188367014-06c21511-f5ae-405e-9a2c-29ebcdac1c25.png)

    
    To verify DB installation please use below steps ,
   
    > docker exec -it cassandra-server-iot /bin/bash
    > cqlsh
    > describe keyspaces;
    
      ![image](https://user-images.githubusercontent.com/7611920/188366869-e1a30a93-42ac-41f1-983e-eda51305c632.png)

     
    If script may not run due to any environment related issues , please run below sql script to create database 
    
    > CREATE KEYSPACE iot_data WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
    
 5. To start API application please find jar file under <project>/target/ iot_data-0.0.1-SNAPSHOT.jar
 
    >  To execute jar -  Java -jar  iot_data-0.0.1-SNAPSHOT.jar
    
    API services will start on localhost with this url  http://localhost:9090/eventApi/events/ 
    
 6. When  Producer application up and running  consumer will listing to broker and capture event and store in Cassendra DB.
 
 ### To Query API
 
 1. Start Postman
 
 2. Application secured with basic Auth since you need to set credentials on Postman to access Event API.
    > User Name -admin , Password – root
    
    ![image](https://user-images.githubusercontent.com/7611920/188364164-63e81279-7752-48bc-8ff3-0071e7757e71.png)
    
    To query Api use below payload and send request on POST method to API end point.
    
    > Endpoint - http://localhost:9090/eventApi/events/ 
    
    > Payload 
    
        {
           "eventType":"TEMPERATURE",
           "formTime":"2022-09-03T08:27:41.549239Z",
           "toTime":"2022-09-03T08:28:11.567034Z",
           "operation":"MAX",
           "clusterId":1
        }
     > Required Fields – eventType , formTime, toTime , operation.
     
     >  Basic Operations – MAX,MIN,AVG,MEDIAN
    
     > Response will be
       
         {
            "eventType": "TEMPERATURE",
            "formTime": "2022-09-03T08:27:41.549239Z",
            "toTime": "2022-09-03T08:28:11.567034Z",
            "operation": "MAX",
            "clusterId": 1,
            "resultsValue": 3.54656566666
         }
         
### Design Architecture
IOT Data Consumer API application based on Spring boot kafka listeners and Casandra data stores .  Producer always generate events and Consumer continues consume  data stream and read data. Data validation doing by schema validation before stored events in Cassandra dara stores as direct JSON objects . API will provide access to data by Secured REST endpoints .

![image](https://user-images.githubusercontent.com/7611920/188365281-a86418a0-482e-4279-a4b0-b32c6a803c29.png)

### Main decisions

Spring boot kafka listeners use to listing to brokers with concurrency  handling to capable with large no of events. These Properties are configurable .
If required to increase no if topics it can be done by application .properties file .

Use Cassandra as a data store to support 
 - Support scalability of the data stores for large no of event data. Can increase replication factor of  key space.
 - Fast searching , querying facility  based on Partition keys . composite partition keys using data modeling to support searching fields .
     
Address Scalability and extendibility .

- Use direct json ingesting feature in Cassandra  use to reduce dependency of the data entity level.
     
- If need to add new event type, we only need to alter table schema and schema validations according to the support json format.
  
- There is data schema validation doing by casting data set to Data model entity.

### Limitations / Improvements

- Please note API service deployment separated as JAR file execution  from single docker approach .Due to the testing limitation of the docker network this API           Service tested as External Jar execution to make external Traffic to karaka broker (localhost mode).
- Consumer and API services are highly coupled with this design . once scaling application may problem its need to scale whole application .
- To improve it, it’s better to go with decouple strategy to separate Consumer and API service . best approach to use kafka Connectors - Source / Sync connector model   to send data directly to Cassandra Database .

 

     
    
     

  


     


  
