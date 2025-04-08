package ds.edu.cmu.tourtrackerwebservice.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service class for fetching tour information from Ticketmaster API
 */
public class TourService {

    private static final String API_KEY = "V4GVcV42BOhGxEAoO7kEFfGZLs4NYt7p";
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";

    private final Gson gson = new Gson();

    /**
     * Response class for the TourService
     */
    public static class TourResponse {
        private JsonObject result;
        private int statusCode;
        private long responseTimeMs;
        private int eventCount;
        private String errorMessage;

        // Getters and setters
        public JsonObject getResult() {
            return result;
        }

        public void setResult(JsonObject result) {
            this.result = result;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public long getResponseTimeMs() {
            return responseTimeMs;
        }

        public void setResponseTimeMs(long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
        }

        public int getEventCount() {
            return eventCount;
        }

        public void setEventCount(int eventCount) {
            this.eventCount = eventCount;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Fetches tour information for a given artist
     *
     * @param artistName the name of the artist
     * @return a TourResponse object containing the result and metadata
     */
    public TourResponse getTourEvents(String artistName) {
        TourResponse response = new TourResponse();
        long startTime = System.currentTimeMillis();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String query = String.format(
                    "apikey=%s&keyword=%s&size=30&sort=date,asc",
                    URLEncoder.encode(API_KEY, StandardCharsets.UTF_8),
                    URLEncoder.encode(artistName, StandardCharsets.UTF_8)
            );

            String fullUrl = BASE_URL + "?" + query;
            HttpGet getRequest = new HttpGet(fullUrl);

            var httpResponse = httpClient.execute(getRequest);
            int statusCode = httpResponse.getCode();
            response.setStatusCode(statusCode);

            HttpEntity entity = httpResponse.getEntity();
            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            if (statusCode == 200) {
                JsonObject ticketmasterJson = gson.fromJson(responseBody, JsonObject.class);
                JsonArray simplifiedEvents = new JsonArray();

                int eventCount = 0;
                if (ticketmasterJson.has("_embedded")) {
                    JsonArray events = ticketmasterJson
                            .getAsJsonObject("_embedded")
                            .getAsJsonArray("events");

                    eventCount = events.size();
                    response.setEventCount(eventCount);

                    for (JsonElement e : events) {
                        JsonObject event = e.getAsJsonObject();
                        JsonObject simplified = new JsonObject();

                        String localDate = event
                                .getAsJsonObject("dates")
                                .getAsJsonObject("start")
                                .get("localDate").getAsString();

                        String cityName = event
                                .getAsJsonObject("_embedded")
                                .getAsJsonArray("venues")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("city")
                                .get("name").getAsString();

                        simplified.addProperty("date", localDate);
                        simplified.addProperty("city", cityName);
                        simplifiedEvents.add(simplified);
                    }
                } else {
                    response.setEventCount(0);
                }

                JsonObject result = new JsonObject();
                result.add("events", simplifiedEvents);
                response.setResult(result);
            } else {
                response.setErrorMessage("Ticketmaster API error: " + statusCode);
                response.setEventCount(0);
            }

        } catch (IOException e) {
            response.setStatusCode(500);
            response.setErrorMessage("Error accessing Ticketmaster API: " + e.getMessage());
            response.setEventCount(0);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setErrorMessage("Internal server error: " + e.getMessage());
            response.setEventCount(0);
        } finally {
            response.setResponseTimeMs(System.currentTimeMillis() - startTime);
        }

        return response;
    }
}