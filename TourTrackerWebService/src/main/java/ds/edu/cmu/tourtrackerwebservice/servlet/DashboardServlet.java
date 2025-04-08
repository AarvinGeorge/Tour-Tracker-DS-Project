package ds.edu.cmu.tourtrackerwebservice.servlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import ds.edu.cmu.tourtrackerwebservice.db.MongoDBConnection;
import ds.edu.cmu.tourtrackerwebservice.model.RequestLogEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get log collection
        MongoCollection<Document> logCollection = MongoDBConnection.getLogCollection();

        // Get recent logs (up to 100)
        List<RequestLogEntry> recentLogs = getRecentLogs(logCollection, 100);
        request.setAttribute("recentLogs", recentLogs);

        // Get top 5 searched artists
        List<Document> topArtists = getTopArtists(logCollection, 5);
        request.setAttribute("topArtists", topArtists);

        // Get average response time
        double avgResponseTime = getAverageResponseTime(logCollection);
        request.setAttribute("avgResponseTime", avgResponseTime);

        // Get device type distribution
        List<Document> deviceDistribution = getDeviceDistribution(logCollection);
        request.setAttribute("deviceDistribution", deviceDistribution);

        // Forward to the dashboard JSP
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    /**
     * Get the most recent log entries
     *
     * @param collection the log collection
     * @param limit maximum number of logs to retrieve
     * @return list of RequestLogEntry objects
     */
    private List<RequestLogEntry> getRecentLogs(MongoCollection<Document> collection, int limit) {
        List<RequestLogEntry> logs = new ArrayList<>();

        collection.find()
                .sort(Sorts.descending("timestamp"))
                .limit(limit)
                .forEach(doc -> logs.add(RequestLogEntry.fromDocument(doc)));

        return logs;
    }

    /**
     * Get the top searched artists
     *
     * @param collection the log collection
     * @param limit maximum number of artists to retrieve
     * @return list of documents containing artist name and count
     */
    private List<Document> getTopArtists(MongoCollection<Document> collection, int limit) {
        List<Document> topArtists = new ArrayList<>();

        collection.aggregate(Arrays.asList(
                Aggregates.match(new Document("artistName", new Document("$ne", null))),
                Aggregates.group("$artistName", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(limit)
        )).forEach(topArtists::add);

        return topArtists;
    }

    /**
     * Get the average response time in milliseconds
     *
     * @param collection the log collection
     * @return average response time
     */
    private double getAverageResponseTime(MongoCollection<Document> collection) {
        Document result = collection.aggregate(Arrays.asList(
                Aggregates.group(null, Accumulators.avg("avgResponseTime", "$responseTimeMs"))
        )).first();

        return (result != null && result.containsKey("avgResponseTime"))
                ? result.getDouble("avgResponseTime") : 0.0;
    }

    /**
     * Get device type distribution
     *
     * @param collection the log collection
     * @return list of documents containing device type and count
     */
    private List<Document> getDeviceDistribution(MongoCollection<Document> collection) {
        List<Document> deviceDistribution = new ArrayList<>();

        collection.aggregate(Arrays.asList(
                // Add a field that extracts device type from User-Agent
                Aggregates.project(Projections.fields(
                        Projections.computed("deviceType",
                                new Document("$cond", Arrays.asList(
                                        new Document("$regexMatch",
                                                new Document("input", "$deviceInfo")
                                                        .append("regex", "Android")),
                                        "Android",
                                        new Document("$cond", Arrays.asList(
                                                new Document("$regexMatch",
                                                        new Document("input", "$deviceInfo")
                                                                .append("regex", "iPhone|iPad|iOS")),
                                                "iOS",
                                                "Other"
                                        ))
                                ))
                        )
                )),
                // Group by device type
                Aggregates.group("$deviceType", Accumulators.sum("count", 1)),
                // Sort by count descending
                Aggregates.sort(Sorts.descending("count"))
        )).forEach(deviceDistribution::add);

        return deviceDistribution;
    }
}