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
        holder.clientName.setText(clientText);
        
        // Set address
        holder.address.setText(item.getAddress());

        // Handle first and last items differently for the timeline line
        if (position == 0) {
            // First item: line starts from the middle
            holder.timelineLine.setY(holder.timelineDot.getHeight() / 2f);
            ViewGroup.LayoutParams params = holder.timelineLine.getLayoutParams();
            params.height = holder.itemView.getHeight() - holder.timelineDot.getHeight() / 2;
            holder.timelineLine.setLayoutParams(params);
        } else if (position == items.size() - 1) {
            // Last item: line ends at the middle
            ViewGroup.LayoutParams params = holder.timelineLine.getLayoutParams();
            params.height = holder.timelineDot.getHeight() / 2;
            holder.timelineLine.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView clientName;
        TextView address;
        View timelineLine;
        View timelineDot;

        TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.client_name);
            address = itemView.findViewById(R.id.address);
            timelineLine = itemView.findViewById(R.id.timeline_line);
            timelineDot = itemView.findViewById(R.id.timeline_dot);
        }
    }
}