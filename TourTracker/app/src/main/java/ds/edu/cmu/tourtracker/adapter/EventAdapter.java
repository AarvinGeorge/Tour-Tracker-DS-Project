package ds.edu.cmu.tourtracker.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ds.edu.cmu.tourtracker.R;
import ds.edu.cmu.tourtracker.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> eventList;

    public EventAdapter(List<Event> events) {
        this.eventList = events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textEvent;

        public ViewHolder(TextView view) {
            super(view);
            textEvent = view;
        }

        public void bind(Event event) {
            textEvent.setText(event.getCity() + " - " + event.getDate());
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(eventList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
