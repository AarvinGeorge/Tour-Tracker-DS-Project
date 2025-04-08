package ds.edu.cmu.tourtrackerwebservice.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import jakarta.servlet.http.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class TourServlet extends HttpServlet {

    private static final String API_KEY = "V4GVcV42BOhGxEAoO7kEFfGZLs4NYt7p";
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String artistName = request.getParameter("artistName");

        if (artistName == null || artistName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing or empty artistName parameter\"}");
            return;
        }

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

            if (statusCode != 200) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().write("{\"error\": \"Ticketmaster API error: " + statusCode + "\"}");
                return;
            }

            HttpEntity entity = httpResponse.getEntity();
            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            JsonObject ticketmasterJson = gson.fromJson(responseBody, JsonObject.class);
            JsonArray simplifiedEvents = new JsonArray();

            if (ticketmasterJson.has("_embedded")) {
                JsonArray events = ticketmasterJson
                        .getAsJsonObject("_embedded")
                        .getAsJsonArray("events");

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
            }

            JsonObject result = new JsonObject();
            result.add("events", simplifiedEvents);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error. Please try again later.\"}");
        }
    }
}
