package ds.edu.cmu.tourtrackerwebservice.test;

import com.mongodb.client.MongoCollection;
import ds.edu.cmu.tourtrackerwebservice.db.MongoDBConnection;
import ds.edu.cmu.tourtrackerwebservice.model.RequestLogEntry;
import org.bson.Document;

/**
 * Simple test for MongoDB connection and operations.
 * This class is not part of the main application, but can be run to verify MongoDB setup.
 */
public class MongoDBTest {

    public static void main(String[] args) {
        try {
            System.out.println("Testing MongoDB connection...");

            // Test connection
            MongoCollection<Document> collection = MongoDBConnection.getLogCollection();
            System.out.println("Successfully connected to collection: " + collection.getNamespace());

            // Count documents
            long count = collection.countDocuments();
            System.out.println("Current document count: " + count);

            // Insert a test document
            RequestLogEntry testEntry = new RequestLogEntry();
            testEntry.setArtistName("Test Artist");
            testEntry.setDeviceInfo("Test Device");
            testEntry.setIpAddress("127.0.0.1");
            testEntry.setResponseStatus(200);
            testEntry.setEventCount(5);
            testEntry.setResponseTimeMs(150);
            testEntry.setTicketmasterResponseStatus("200");

            collection.insertOne(testEntry.toDocument());
            System.out.println("Inserted test document");

            // Verify count increased
            long newCount = collection.countDocuments();
            System.out.println("New document count: " + newCount);

            if (newCount > count) {
                System.out.println("✅ MongoDB test successful!");
            } else {
                System.out.println("❌ Document count did not increase. Check MongoDB configuration.");
            }

            // Clean up - delete test document
            collection.deleteOne(new Document("artistName", "Test Artist")
                    .append("deviceInfo", "Test Device"));
            System.out.println("Removed test document");

            // Close connection
            MongoDBConnection.closeConnection();

        } catch (Exception e) {
            System.err.println("MongoDB Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}