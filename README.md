# Hola 
___

## Usage :
Hola is a Text messeging java Application.First start the Hola client and then enter the server address of Hola. To send SMS first user need to signup for Hola.Then after login user need to add mobile number of the another user.If another user is already registered for hola then user name will be appeared in chat section.You will see the name of user as Anonymous if the added contact in not a Hola user.Now in order to send messesge both the sender and receiver must add contact of each other.If the receiver did not added the sender contact the ther receiver will only see the notification of sender message. 


## Technical Detail: 
This Application is build using client-server architecture of RMI(Remote Method Invocation) API where server is also act as client.It is a real time chat application in which first client sent messege to rmi server then first message gets saved in database and then again send message to specified client.hashcode of the class is used to identify the client.Ever time the client login into app then its hascode gets updated in server and database as hascode of java class changes on every execution.The moment user logout from the hola app his/her hascode gets deleted from the server and user becomes offline. 

___

## Technology Used :
* JavaFx
* Java RMI(Remote method Invocation)
* Messaging Gatway
* MongoDB noSql

