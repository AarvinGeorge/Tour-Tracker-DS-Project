package ds.edu.cmu.tourtrackerwebservice.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Utility class for managing MongoDB connections
 */
public class MongoDBConnection {

    private static final String CONNECTION_STRING = "mongodb://tomgrg8:petwvSkFpu2YunME@cluster0-shard-00-02.esb4f.mongodb.net:27017,cluster0-shard-00-01.esb4f.mongodb.net:27017,cluster0-shard-00-00.esb4f.mongodb.net:27017/tour-tracker-db?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
    private static final String DATABASE_NAME = "tour-tracker-db";
    private static final String LOG_COLLECTION_NAME = "request_logs";

    private static MongoClient mongoClient;

    // Private constructor to prevent instantiation
    private MongoDBConnection() {}

    /**
     * Gets or creates a MongoDB client instance
     *
     * @return MongoClient instance
     */
    public static synchronized MongoClient getMongoClient() {
        if (mongoClient == null) {
            try {
                ConnectionString connString = new ConnectionString(CONNECTION_STRING);

                // Configure MongoDB client settings
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connString)
                        .serverApi(ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build())
                        .build();

                // Create new client
                mongoClient = MongoClients.create(settings);

                // Log successful connection
                System.out.println("Successfully connected to MongoDB Atlas");
            } catch (Exception e) {
                System.err.println("MongoDB Connection Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mongoClient;
    }

    /**
     * Gets the MongoDB database
     *
     * @return MongoDatabase instance
     */
    public static MongoDatabase getDatabase() {
        return getMongoClient().getDatabase(DATABASE_NAME);
    }

    /**
     * Gets the log collection
     *
     * @return MongoCollection for storing request logs
     */
    public static MongoCollection<Document> getLogCollection() {
        return getDatabase().getCollection(LOG_COLLECTION_NAME);
    }

    /**
     * Closes the MongoDB client connection
     */
    public static synchronized void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            System.out.println("MongoDB connection closed");
        }
    }
}