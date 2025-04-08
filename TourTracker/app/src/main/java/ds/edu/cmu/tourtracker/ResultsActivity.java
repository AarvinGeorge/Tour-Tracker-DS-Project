package ds.edu.cmu.tourtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import ds.edu.cmu.tourtracker.adapter.EventAdapter;
import ds.edu.cmu.tourtracker.model.Event;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerResults;
    private MaterialButton btnBack;
    private ProgressBar progressBar;
    private View rootView;

    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        rootView = findViewById(android.R.id.content);
        btnBack = findViewById(R.id.btnBackToSearch);
        recyclerResults = findViewById(R.id.recyclerResults);
        progressBar = findViewById(R.id.progressBar);

        recyclerResults.setLayoutManager(new LinearLayoutManager(this)); // ✅ This enables vertical scrolling


        artistName = getIntent().getStringExtra("artistName");

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        fetchTourData(artistName);
    }

    private void fetchTourData(String artistName) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                String endpoint = "http://10.0.2.2:8080/TourTrackerWebService/getTour?artistName=" + URLEncoder.encode(artistName, "UTF-8");
                Log.d("TourTracker", "Fetching from endpoint: " + endpoint);

                URL url = new URL(endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                Log.d("TourTracker", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);

                    Gson gson = new Gson();
                    EventResponse response = gson.fromJson(reader, EventResponse.class);
                    inputStream.close();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (response != null && response.events != null) {
                            recyclerResults.setAdapter(new EventAdapter(response.events));
                        } else {
                            showSnackbar("No events found.");
                            Log.w("TourTracker", "Response or events list was null");
                        }
                    });

                } else {
                    showSnackbar("Server error: " + responseCode);
                    Log.e("TourTracker", "Non-200 response from server");
                }

            } catch (JsonSyntaxException e) {
                Log.e("TourTracker", "JSON parsing failed", e);
                showSnackbar("Invalid JSON format from server.");
            } catch (Exception e) {
                Log.e("TourTracker", "Networking failed", e);
                showSnackbar("Error fetching data.");
            }
        }).start();
    }

    private void showSnackbar(String msg) {
        new Handler(Looper.getMainLooper()).post(() -> {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show();
        });
    }

    // Helper inner class for top-level JSON structure
    public class EventResponse {
        List<Event> events;
    }
}
