# springboot-gcs-signed-url
Upload file to GCS, create URL which expires using Spring Cloud

To Run: 

>> mvn clean spring-boot:run


Replace Sample.txt with file that needs to be uploaded

To upload:

>> curl --location --request POST 'http://localhost:8080/upload' --form 'file=@/Users/Sample.txt'
