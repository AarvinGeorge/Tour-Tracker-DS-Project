package ds.edu.cmu.tourtrackerwebservice.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.*;

import ds.edu.cmu.tourtrackerwebservice.db.MongoDBConnection;
import ds.edu.cmu.tourtrackerwebservice.model.RequestLogEntry;
import ds.edu.cmu.tourtrackerwebservice.service.TourService;
import ds.edu.cmu.tourtrackerwebservice.service.TourService.TourResponse;

import java.io.IOException;

/**
 * Servlet for handling tour information requests
 */
public class TourServlet extends HttpServlet {

    private final TourService tourService = new TourService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Start tracking response time
        long startTime = System.currentTimeMillis();

        // Create log entry
        RequestLogEntry logEntry = new RequestLogEntry();
        logEntry.setIpAddress(request.getRemoteAddr());
        logEntry.setDeviceInfo(request.getHeader("User-Agent"));

        String artistName = request.getParameter("artistName");
        logEntry.setArtistName(artistName);

        if (artistName == null || artistName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing or empty artistName parameter\"}");

            // Log the error
            logEntry.setResponseStatus(HttpServletResponse.SC_BAD_REQUEST);
            logEntry.setEventCount(0);
            logEntry.setTicketmasterResponseStatus("Not Called - Missing Parameter");
            logEntry.setResponseTimeMs(System.currentTimeMillis() - startTime);
            logRequestToMongoDB(logEntry);

            return;
        }

        try {
            // Call the TourService to fetch tour information
            TourResponse tourResponse = tourService.getTourEvents(artistName);

            // Log Ticketmaster API response status
            logEntry.setTicketmasterResponseStatus(String.valueOf(tourResponse.getStatusCode()));

            if (tourResponse.getStatusCode() != 200) {
                // Handle API error
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().write("{\"error\": \"" + tourResponse.getErrorMessage() + "\"}");

                // Log the error
                logEntry.setResponseStatus(HttpServletResponse.SC_BAD_GATEWAY);
                logEntry.setEventCount(0);
                logEntry.setResponseTimeMs(System.currentTimeMillis() - startTime);
                logRequestToMongoDB(logEntry);

                return;
            }

            // Write successful response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(tourResponse.getResult()));

            // Log successful response
            logEntry.setResponseStatus(HttpServletResponse.SC_OK);
            logEntry.setEventCount(tourResponse.getEventCount());
            logEntry.setResponseTimeMs(System.currentTimeMillis() - startTime);
            logRequestToMongoDB(logEntry);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error. Please try again later.\"}");

            // Log the error
            logEntry.setResponseStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logEntry.setEventCount(0);
            logEntry.setTicketmasterResponseStatus("Error: " + e.getMessage());
            logEntry.setResponseTimeMs(System.currentTimeMillis() - startTime);
            logRequestToMongoDB(logEntry);
        }
    }

    /**
     * Logs request data to MongoDB
     *
     * @param logEntry the log entry to store
     */
    private void logRequestToMongoDB(RequestLogEntry logEntry) {
        try {
            MongoDBConnection.getLogCollection().insertOne(logEntry.toDocument());
        } catch (Exception e) {
            System.err.println("Error logging to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}