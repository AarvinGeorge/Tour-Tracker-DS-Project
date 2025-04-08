package ds.edu.cmu.tourtrackerwebservice.model;

import org.bson.Document;
import java.util.Date;

/**
 * Model class for request log entries
 */
public class RequestLogEntry {
    private Date timestamp;
    private String artistName;
    private String deviceInfo;
    private String ipAddress;
    private int responseStatus;
    private int eventCount;
    private long responseTimeMs;
    private String ticketmasterResponseStatus;

    // Default constructor
    public RequestLogEntry() {
        this.timestamp = new Date();
    }

    // Getters and setters
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getTicketmasterResponseStatus() {
        return ticketmasterResponseStatus;
    }

    public void setTicketmasterResponseStatus(String ticketmasterResponseStatus) {
        this.ticketmasterResponseStatus = ticketmasterResponseStatus;
    }

    /**
     * Convert this RequestLogEntry to a MongoDB Document
     *
     * @return Document representation of this log entry
     */
    public Document toDocument() {
        return new Document("timestamp", this.timestamp)
                .append("artistName", this.artistName)
                .append("deviceInfo", this.deviceInfo)
                .append("ipAddress", this.ipAddress)
                .append("responseStatus", this.responseStatus)
                .append("eventCount", this.eventCount)
                .append("responseTimeMs", this.responseTimeMs)
                .append("ticketmasterResponseStatus", this.ticketmasterResponseStatus);
    }

    /**
     * Create a RequestLogEntry from a MongoDB Document
     *
     * @param document the document to convert
     * @return a new RequestLogEntry
     */
    public static RequestLogEntry fromDocument(Document document) {
        RequestLogEntry entry = new RequestLogEntry();
        entry.setTimestamp(document.getDate("timestamp"));
        entry.setArtistName(document.getString("artistName"));
        entry.setDeviceInfo(document.getString("deviceInfo"));
        entry.setIpAddress(document.getString("ipAddress"));
        entry.setResponseStatus(document.getInteger("responseStatus"));
        entry.setEventCount(document.getInteger("eventCount"));
        entry.setResponseTimeMs(document.getLong("responseTimeMs"));
        entry.setTicketmasterResponseStatus(document.getString("ticketmasterResponseStatus"));
        return entry;
    }
}