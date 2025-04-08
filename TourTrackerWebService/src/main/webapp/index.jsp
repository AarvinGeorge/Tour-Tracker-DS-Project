<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>TourTracker Web Service</title>
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
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 5px;
            text-align: center;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            background-color: #3498db;
            color: white;
            padding: 10px 20px;
            margin: 20px 0;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .api-info {
            text-align: left;
            margin-top: 30px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;
            border-left: 4px solid #3498db;
        }
        .api-info h2 {
            margin-top: 0;
            color: #2c3e50;
        }
        code {
            background-color: #f0f0f0;
            padding: 2px 5px;
            border-radius: 3px;
            font-family: monospace;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>TourTracker Web Service</h1>
    <p>Welcome to the TourTracker Web Service, powered by Ticketmaster API.</p>

    <a href="dashboard" class="btn">View Operations Dashboard</a>

    <div class="api-info">
        <h2>API Endpoint</h2>
        <p>To get tour information for an artist, make a GET request to:</p>
        <code>/getTour?artistName={artistName}</code>

        <h2>Example Response</h2>
        <pre><code>{
  "events": [
    {
      "date": "2025-04-15",
      "city": "New York"
    },
    {
      "date": "2025-04-18",
      "city": "Boston"
    }
  ]
}</code></pre>
    </div>
</div>
</body>
</html>