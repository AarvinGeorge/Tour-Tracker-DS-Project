package ds.edu.cmu.tourtrackerwebservice.listener;

import ds.edu.cmu.tourtrackerwebservice.db.MongoDBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Servlet context listener to manage MongoDB connection lifecycle
 */
public class MongoDBContextListener implements ServletContextListener {

    /**
     * Initialize MongoDB connection when the web application starts
     *
     * @param sce the ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing MongoDB connection...");
        // Initialize the MongoDB connection
        MongoDBConnection.getMongoClient();
    }

    /**
     * Close MongoDB connection when the web application shuts down
     *
     * @param sce the ServletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Closing MongoDB connection...");
        // Close the MongoDB connection
        MongoDBConnection.closeConnection();
    }
}