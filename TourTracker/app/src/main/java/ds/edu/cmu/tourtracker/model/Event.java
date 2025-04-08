package ds.edu.cmu.tourtracker.model;

public class Event {
    private String city;
    private String date;

    // Required by Gson
    public Event() {}

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }
}
