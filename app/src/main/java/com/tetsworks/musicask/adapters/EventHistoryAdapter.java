package com.tetsworks.musicask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tetsworks.musicask.R;
import com.tetsworks.musicask.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.ViewHolder> {
    private List<Event> events;
    private final Context context;
    private OnEventClickListener listener;
    private final SimpleDateFormat dateFormat;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventHistoryAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.tvEventName.setText(event.getName() != null ? event.getName() : "Evento #" + event.getCode());
        holder.tvEventCode.setText("Código: " + event.getCode());
        holder.tvEventDate.setText(dateFormat.format(new Date(event.getCreatedAt())));
        holder.tvTotalRequests.setText(event.getTotalRequests() + " pedidos");

        if (event.isActive()) {
            holder.tvEventStatus.setText("ATIVO");
            holder.tvEventStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvEventStatus.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvEventName;
        TextView tvEventCode;
        TextView tvEventDate;
        TextView tvTotalRequests;
        TextView tvEventStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventCode = itemView.findViewById(R.id.tvEventCode);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvTotalRequests = itemView.findViewById(R.id.tvTotalRequests);
            tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
        }
    }
}
