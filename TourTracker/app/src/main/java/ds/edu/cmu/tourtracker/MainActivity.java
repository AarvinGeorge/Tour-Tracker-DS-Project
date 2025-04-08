package ds.edu.cmu.tourtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editArtistName;
    private MaterialButton btnTrackTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        editArtistName = findViewById(R.id.editArtistName);
        btnTrackTour = findViewById(R.id.btnTrackTour);

        btnTrackTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String artist = editArtistName.getText().toString().trim();

                if (artist.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter an artist name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                intent.putExtra("artistName", artist);
                startActivity(intent);
            }
        });
    }
}
