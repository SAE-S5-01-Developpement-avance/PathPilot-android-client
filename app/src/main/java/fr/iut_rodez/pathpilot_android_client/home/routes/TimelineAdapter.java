package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
//        switch (item.getState()) {
//            case VISITED:
//                holder.timelineDot.setBackgroundResource(R.drawable.visited_dot);
//                break;
//            case NOT_VISITED:
//                holder.timelineDot.setBackgroundResource(R.drawable.circle_dot);
//                break;
//            case SKIPPED:
//                holder.timelineDot.setBackgroundResource(R.drawable.skipped_dot);
//                break;
//        }

        // Modify timeline line for last item to add arrow
        if (position == items.size() - 1) {
            ViewGroup.LayoutParams params = holder.timelineLine.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.timelineLine.setLayoutParams(params);
            holder.timelineLine.setBackground(createArrowDrawable(holder.itemView.getContext()));
        }
    }

    /**
     * Create an arrow drawable
     *
     * @param context Context of the application
     * @return Drawable with an arrow at the bottom
     */
    private Drawable createArrowDrawable(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#0093B8"));
        drawable.setShape(GradientDrawable.RECTANGLE);

        // Create a shape with an arrow at the bottom
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(10, 0);
        path.lineTo(5, 10);
        path.close();

        return drawable;
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

        TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            clientInfo = itemView.findViewById(R.id.timeline_client_info);
            address = itemView.findViewById(R.id.timeline_client_address);
            timelineLine = itemView.findViewById(R.id.timeline_line);
            timelineDot = itemView.findViewById(R.id.timeline_dot);
        }
    }
}