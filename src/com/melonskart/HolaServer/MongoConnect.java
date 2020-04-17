package com.melonskart.HolaServer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnect {

    private static MongoClient  mongoClient ;


    public static MongoCollection<Document>  getCollection(String collectionName) {


        mongoClient = new MongoClient("127.0.0.1", 27017);
//            System.out.println("Connected to Mongo db");

        MongoDatabase mongoDatabase = mongoClient.getDatabase("hola");
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        return collection;
    }

    public static boolean closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            return true;
        }
        return false;
    }
}
