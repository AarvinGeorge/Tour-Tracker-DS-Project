<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ds.edu.cmu.tourtrackerwebservice.model.RequestLogEntry" %>
<%@ page import="java.util.List" %>
<%@ page import="org.bson.Document" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TourTracker Dashboard</title>
  <style>
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      line-height: 1.6;
      color: #333;
      margin: 0;
      padding: 20px;
      background-color: #f5f5f5;
    }
    .container {
      max-width: 1200px;
      margin: 0 auto;
      background-color: #fff;
      padding: 20px;
      box-shadow: 0 0 10px rgba(0,0,0,0.1);
      border-radius: 5px;
    }
    h1 {
      color: #2c3e50;
      margin-bottom: 30px;
      padding-bottom: 10px;
      border-bottom: 2px solid #3498db;
    }
    h2 {
      color: #2c3e50;
      margin-top: 25px;
    }
    .analytics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }
    .analytics-card {
      background-color: #fff;
      border: 1px solid #ddd;
      border-radius: 4px;
      padding: 15px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .analytics-card h3 {
      margin-top: 0;
      color: #3498db;
      font-size: 18px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      background-color: #fff;
    }
    th, td {
      text-align: left;
      padding: 12px;
      border-bottom: 1px solid #ddd;
    }
    th {
      background-color: #3498db;
      color: white;
    }
    tr:hover {
      background-color: #f5f5f5;
    }
    .status-success {
      color: green;
    }
    .status-error {
      color: red;
    }
    .refresh-link {
      display: inline-block;
      margin-top: 20px;
      background-color: #3498db;
      color: white;
      padding: 10px 15px;
      text-decoration: none;
      border-radius: 4px;
      transition: background-color 0.3s;
    }
    .refresh-link:hover {
      background-color: #2980b9;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>TourTracker Operations Dashboard</h1>

  <!-- Analytics Section -->
  <h2>Operations Analytics</h2>
  <div class="analytics-grid">
    <!-- Top 5 Artists -->
    <div class="analytics-card">
      <h3>Top Searched Artists</h3>
      <table>
        <tr>
          <th>Artist</th>
          <th>Searches</th>
        </tr>
        <%
          List<Document> topArtists = (List<Document>) request.getAttribute("topArtists");
          if (topArtists != null && !topArtists.isEmpty()) {
            for (Document artist : topArtists) {
        %>
        <tr>
          <td><%= artist.getString("_id") %></td>
          <td><%= artist.getInteger("count") %></td>
        </tr>
        <%
          }
        } else {
        %>
        <tr>
          <td colspan="2">No artist data available</td>
        </tr>
        <% } %>
      </table>
    </div>

    <!-- Average Response Time -->
    <div class="analytics-card">
      <h3>Response Time</h3>
      <%
        double avgResponseTime = (Double) request.getAttribute("avgResponseTime");
        DecimalFormat df = new DecimalFormat("#.00");
      %>
      <p>Average API Response Time: <strong><%= df.format(avgResponseTime) %> ms</strong></p>
    </div>

    <!-- Device Distribution -->
    <div class="analytics-card">
      <h3>Device Distribution</h3>
      <table>
        <tr>
          <th>Device Type</th>
          <th>Count</th>
        </tr>
        <%
          List<Document> deviceDistribution = (List<Document>) request.getAttribute("deviceDistribution");
          if (deviceDistribution != null && !deviceDistribution.isEmpty()) {
            for (Document device : deviceDistribution) {
        %>
        <tr>
          <td><%= device.getString("_id") %></td>
          <td><%= device.getInteger("count") %></td>
        </tr>
        <%
          }
        } else {
        %>
        <tr>
          <td colspan="2">No device data available</td>
        </tr>
        <% } %>
      </table>
    </div>
  </div>

  <!-- Logs Section -->
  <h2>Request Logs</h2>
  <table>
    <tr>
      <th>Timestamp</th>
      <th>Artist</th>
      <th>Client Device</th>
      <th>Status</th>
      <th>Events</th>
      <th>Response Time (ms)</th>
      <th>Ticketmaster Status</th>
    </tr>
    <%
      List<RequestLogEntry> logs = (List<RequestLogEntry>) request.getAttribute("recentLogs");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      if (logs != null && !logs.isEmpty()) {
        for (RequestLogEntry log : logs) {
          String statusClass = (log.getResponseStatus() == 200) ? "status-success" : "status-error";
    %>
    <tr>
      <td><%= dateFormat.format(log.getTimestamp()) %></td>
      <td><%= log.getArtistName() != null ? log.getArtistName() : "N/A" %></td>
      <td><%= log.getDeviceInfo() != null ? log.getDeviceInfo() : "Unknown" %></td>
      <td class="<%= statusClass %>"><%= log.getResponseStatus() %></td>
      <td><%= log.getEventCount() %></td>
      <td><%= log.getResponseTimeMs() %></td>
      <td><%= log.getTicketmasterResponseStatus() != null ? log.getTicketmasterResponseStatus() : "N/A" %></td>
    </tr>
    <%
      }
    } else {
    %>
    <tr>
      <td colspan="7">No logs available</td>
    </tr>
    <% } %>
  </table>

  <a href="dashboard" class="refresh-link">Refresh Dashboard</a>
</div>
</body>
</html>