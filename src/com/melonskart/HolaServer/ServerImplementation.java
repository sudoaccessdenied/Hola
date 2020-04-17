package com.melonskart.HolaServer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.melonskart.HolaClient.Controller.ClientCallBackInterface;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
// Enter your Auth Key For otp send
public class ServerImplementation extends UnicastRemoteObject implements ServerInterface , Serializable {


    private  HashMap<Integer, ClientCallBackInterface> map;
    @FXML
//    private Text noOfUser;


    private int userCount;

     protected ServerImplementation() throws RemoteException {

        map = new HashMap<>();
        userCount = 0;
    }


    @Override
    synchronized public String getName(long mobile) throws RemoteException {
        String name = "Anonymous";

        try {
            MongoCollection<Document> otpCollection = MongoConnect.getCollection("user");

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);
            MongoCursor<Document> cursor = otpCollection.find(whereQuery).iterator();

            if (cursor.hasNext()) {
                name = cursor.next().getString("name");
            }
            System.out.println( "name "+name);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            MongoConnect.closeConnection();
        }



        return name;
    }

    @Override
    synchronized public boolean registorClient(ClientCallBackInterface client) throws RemoteException {
        map.put(client.hashCode(),client);
        updateHashCode(client.getMobile(), client.hashCode());

        userCount++;
//        noOfUser.setText(String.valueOf(userCount));
        System.out.println(client.getMobile());
        return true;
    }

    synchronized private void updateHashCode(long mobile, int hashCode) {
        try {

            MongoCollection<Document> collection = MongoConnect.getCollection("user");
            UpdateResult updateResult = collection.updateOne(eq("mobile", mobile), Updates.set("hashcode", hashCode));


                    System.out.println("Updated Succefully" + updateResult.getModifiedCount());

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            MongoConnect.closeConnection();
        }

    }



    @Override
    synchronized public boolean unRegistorClient(ClientCallBackInterface clientCallBackInterface) throws RemoteException {

        map.remove(clientCallBackInterface.hashCode());
        userCount--;
//        noOfUser.setText(String.valueOf(userCount));
        return true;
    }

    @Override
    synchronized public boolean createUser(long mobile,String name, String password, int otp) throws RemoteException {

        try {

            MongoCollection<Document> collection = MongoConnect.getCollection("user");
            MongoCollection<Document> connect = MongoConnect.getCollection("connection");
            MongoCollection<Document> otpCollection = MongoConnect.getCollection("otp");

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);

            MongoCursor<Document> cursor = otpCollection.find(whereQuery).iterator();


            if (cursor.hasNext()) {

                int dbOTP = cursor.next().getInteger("otp");
                if (dbOTP != otp) {
                    return false;
                }

            }

            Document doc = new Document();
            doc.append("mobile", mobile);
            doc.append("name", name);
            doc.append("status", true);
            doc.append("password", password);
            doc.append("created_at", new Date());
            doc.append("updated_at", new Date());
            doc.append("hashcode", 123456);

            Document connection = new Document("mobile", mobile);
            connection.append("friends", new ArrayList<Object>());

            connect.insertOne(connection);
            collection.insertOne(doc);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            MongoConnect.closeConnection();
        }


        return true;
    }


    @Override
    synchronized public boolean sendOTP(long mobile)throws RemoteException {
        int randomPin   =(int) (Math.random()*9000)+1000;
        String msg = "Welcome%20to%20Hola%20App.Your%20OTP%20is%20%20"+randomPin;

        try {
            MongoCollection<Document> collection = MongoConnect.getCollection("user");

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);

            MongoCursor<Document> cursor = collection.find(whereQuery).iterator();


            // return false if user already exits...
            if (cursor.hasNext()) {
                return false;
            }


            MongoCollection<Document> collection1 = MongoConnect.getCollection("otp");

            Document document = new Document("mobile", mobile)
                    .append("otp", randomPin)
                    .append("expire_at", new Date())
                    .append("updated_at", new Date());
            collection1.insertOne(document);

            sendText(Long.toString(mobile), msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            MongoConnect.closeConnection();
        }

        return true;
    }

    @Override
    synchronized public boolean loginUser(long mobile, String password) throws RemoteException {

        try {
            MongoCollection<Document> collection = MongoConnect.getCollection("user");

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);
            MongoCursor<Document> cursor = collection.find(whereQuery).iterator();
            if (cursor.hasNext()) {

                String pwd = cursor.next().getString("password");

                if (pwd.equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            MongoConnect.closeConnection();
        }


      return  false;

    }

    @Override
     synchronized public HashMap<Long, String> friend(long mobile) throws RemoteException {

        HashMap<Long, String> map = new HashMap<>();
        List<Document> lists ;

        try {
            MongoCollection<Document> collection = MongoConnect.getCollection("connection");
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);

            List<Document> cursor = collection.find(whereQuery).into(new ArrayList<Document>());


            lists = (List<Document>) cursor.get(0).get("friends");

            for (Document list : lists) {

                map.put(list.getLong("contact"), getName(list.getLong("contact")));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        return map;


    }

    @Override
    synchronized public Document messages(long from, long mobile) throws RemoteException {
        MongoCollection<Document> collection = MongoConnect.getCollection("connection");

        Bson projection = Projections.fields( excludeId(),
                 Projections.elemMatch("friends", eq("contact", mobile))
                ); // Add Projections
//        Document iterable = collection.find(and(eq("mobile", from), elemMatch("friends", eq("mobile", mobile))
//        )).first();

        Document iterable = collection.find(eq("mobile",from)).projection(projection).first();



//        List<Document> object  = cursor.get(0).get("friends");

        System.out.println(" getting Messages"+ iterable);

        return iterable ;
    }

    @Override
    synchronized  public String addContacts(long currentUser, long userToAdd) throws RemoteException {

        try {
            MongoCollection<Document> collection = MongoConnect.getCollection("connection");
            BasicDBObject query = new BasicDBObject("mobile", currentUser);

            BasicDBObject  find = new BasicDBObject("contact", userToAdd);
            BasicDBObject  friend = new BasicDBObject("$elemMatch", find);
            BasicDBObject  elemMatch = new BasicDBObject("friends", friend);

            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
            obj.add(query);
            obj.add(elemMatch);
            BasicDBObject isExist = new BasicDBObject("$and", obj);
//            objectList.append("blocked", false);
//            objectList.append("messages", new ArrayList<Object>());

//            BasicDBObject friends = new BasicDBObject("friends", objectList);




//            Bson projection = Projections.fields( excludeId(), include("friends"),
//                    and(eq("mobile",currentUser), elemMatch("friends", eq("contact", userToAdd))
//                    ));

            Document document = null;
            document = collection.find(isExist).first();

            System.out.println("Output " + document);

            if (document== null) {
                BasicDBObject objectList = new BasicDBObject("contact", userToAdd);
                objectList.append("blocked", false);
                objectList.append("count", 0);
                objectList.append("messages", new ArrayList<Object>());

                BasicDBObject friends = new BasicDBObject("friends", objectList);

                UpdateResult updateResult = collection.updateOne(new BasicDBObject("mobile",currentUser),
                        new BasicDBObject("$push", friends));

                System.out.println("Update done"+ updateResult.getModifiedCount());
                return "200";
            }
        } catch (Exception e) {

            e.printStackTrace();
        }finally {
            MongoConnect.closeConnection();
        }

        return  "404";
    }

    @Override
    synchronized public boolean sendMessage(long from, long to, String msg) throws RemoteException {

        try {
            MongoCollection<Document> collection = MongoConnect.getCollection("connection");

            Bson set =   Updates.inc("friends.$[item].count",1);
            UpdateOptions option = new UpdateOptions().arrayFilters(
                    Arrays.asList(eq("item.contact", to))
            );

            Bson query = and(eq("mobile", from), Filters.elemMatch("friends", eq("contact", to)));
            UpdateResult updateResult = collection.updateOne(query, set,option);

            UpdateOptions option2 = new UpdateOptions().arrayFilters(
                    Arrays.asList(eq("item.contact", from))
            );

            Bson query2 = and(eq("mobile", to), Filters.elemMatch("friends", eq("contact", from)));
            UpdateResult updateResult2 = collection.updateOne(query, set,option);







//            get count query
            Bson projection = Projections.fields( excludeId(),
                     Projections.elemMatch("friends", eq("contact",to))
                    ); // Add Projections
//        Document iterable = collection.find(and(eq("mobile", from), elemMatch("friends", eq("mobile", mobile))
//        )).first();

            Document     iterable = null;
            try {

                iterable =  ((ArrayList<Document>)collection.find(eq("mobile",from)).projection(projection).first().get("friends")).get(0);
                System.out.println("Getting count"+ iterable);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


//

            // push messages
            if (iterable != null) {

                BasicDBObject objectList = new BasicDBObject("msg_id", iterable.getInteger("count"));

                objectList.append("msg_by", from);
                objectList.append("msg", msg);
                objectList.append("send_at", new Date());

//            BasicDBObject friends = new BasicDBObject("friends", objectList);


                Bson msgquery = Updates.push("friends.$[item].messages", objectList);
                UpdateResult updateResult1 = collection.updateOne(query, msgquery, option);


//                Update for both


                Bson query1 = and(eq("mobile", to), Filters.elemMatch("friends", eq("contact", from)));

                BasicDBObject objectList1 = new BasicDBObject("msg_id", iterable.getInteger("count"));

                objectList1.append("msg_by", from);
                objectList1.append("msg", msg);
                objectList1.append("send_at", new Date());

//            BasicDBObject friends = new BasicDBObject("friends", objectList1);
                UpdateOptions option1 = new UpdateOptions().arrayFilters(
                        Arrays.asList(eq("item.contact", from))
                );


                Bson msgquery1 = Updates.push("friends.$[item].messages", objectList1);
                UpdateResult updateResult3 = collection.updateOne(query1, msgquery1, option1);



                int clientHash = getHashCode(to);

                try {
                    ClientCallBackInterface client = map.get(clientHash);


//                    if (client != null) {
                    client.notifyChanges(from, msg, iterable.getInteger("count"), new Date());
                    client.messageAlert(from, msg, iterable.getInteger("count"), new Date());


                } catch (NullPointerException e) {

                    clientHash = getHashCode(from);
                    map.get(clientHash).
                            messageAlert(from, msg + "\n User is Offline", iterable.getInteger("count"), new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }finally {
            MongoConnect.closeConnection();
        }







        return true;
    }

    @Override
    public boolean checkOnline(long to) throws RemoteException {
        return false;
    }

    private int getHashCode(long mobile) {
        int hashCode =0;

        try {
//            mongoClient = new MongoClient("127.0.0.1", 27017);
//            System.out.println("Connected to Mongo db");

//            MongoDatabase mongoDatabase = mongoClient.getDatabase("holadb");
            MongoCollection<Document> collection = MongoConnect.getCollection("user");
//            Document document = new Document("name",name)
//                    .append("msg", msg);
//            documentMongoCollection.insertOne(document);
            //

//            collection.updateOne(Filters.eq("name", name), Updates.set("hashCode", hashCode));

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("mobile", mobile);



            MongoCursor<Document> cursor = collection.find(whereQuery).iterator();


            if (cursor.hasNext()) {

             hashCode=cursor.next().getInteger("hashcode");

                System.out.println("HashCode Found" +hashCode);

                return hashCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            MongoConnect.closeConnection();
        }





        return hashCode;
    }

    private void sendText(String mobile, String msg) {
        try {
            HttpResponse<String> response =
                    Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=MLNKRT&route=4&mobiles="+mobile+"&authkey=your auth key&country=91&message="+msg+"&unicode=1&campaign=API")
                            .asString();
            System.out.println(response);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
