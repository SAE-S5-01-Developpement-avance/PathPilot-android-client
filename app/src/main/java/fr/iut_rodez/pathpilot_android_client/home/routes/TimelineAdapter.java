package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;

/**
 * Adapter for the timeline recycler view
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    private List<TimelineItem> items;
    private Context context;

    /**
     * Constructor for the timeline adapter
     *
     * @param context Context of the application
     * @param items   List of timeline items
     */
    public TimelineAdapter(Context context, List<TimelineItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.component_timeline_item, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineItem item = items.get(position);

        // Set client name and type
        String clientText = item.getClientName() + " (" + item.getClientType() + ")";
        holder.clientInfo.setText(clientText);

        // Set address
        holder.address.setText(item.getAddress().isBlank() ? "NULL" : item.getAddress());

        // Set dot background based on client state TODO add after refactor route
        switch (item.getState()) {
            case VISITED:
                holder.timelineDot.setBackgroundResource(R.drawable.visited_dot);
                break;
            case EXPECTED:
                holder.timelineDot.setBackgroundResource(R.drawable.circle_dot);
                break;
            case SKIPPED:
                holder.timelineDot.setBackgroundResource(R.drawable.skipped_dot);
                break;
        }

        // Handle last item
        if (position == items.size() - 1) {
            holder.timelineArrow.setVisibility(View.VISIBLE);
        } else {
            holder.timelineArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView clientInfo;
        TextView address;
        View timelineLine;
        View timelineDot;
        View timelineArrow;

        TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            clientInfo = itemView.findViewById(R.id.timeline_client_info);
            address = itemView.findViewById(R.id.timeline_client_address);
            timelineLine = itemView.findViewById(R.id.timeline_line);
            timelineDot = itemView.findViewById(R.id.timeline_dot);
            timelineArrow = itemView.findViewById(R.id.timeline_arrow);
        }
    }
}