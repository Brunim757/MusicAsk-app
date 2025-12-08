package com.tetsworks.musicask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tetsworks.musicask.R;
import com.tetsworks.musicask.models.MusicRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<MusicRequest> requests;
    private final Context context;
    private OnRequestClickListener listener;
    private final SimpleDateFormat timeFormat;

    public interface OnRequestClickListener {
        void onRequestClick(MusicRequest request);
    }

    public RequestAdapter(Context context) {
        this.context = context;
        this.requests = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setOnRequestClickListener(OnRequestClickListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<MusicRequest> requests) {
        this.requests = requests != null ? requests : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filterByStatus(List<MusicRequest> allRequests, String status) {
        if (status == null || status.isEmpty()) {
            this.requests = allRequests != null ? allRequests : new ArrayList<>();
        } else {
            this.requests = new ArrayList<>();
            if (allRequests != null) {
                for (MusicRequest request : allRequests) {
                    if (status.equals("later")) {
                        if (request.isLater()) {
                            this.requests.add(request);
                        }
                    } else if (status.equals(request.getStatus())) {
                        this.requests.add(request);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicRequest request = requests.get(position);

        holder.tvTrackName.setText(request.getTrackName());
        holder.tvArtistName.setText(request.getArtistName());
        holder.tvRequesterName.setText(request.getRequesterName());
        holder.tvTime.setText(timeFormat.format(new Date(request.getRequestedAt())));
        holder.tvStatus.setText(request.getStatusDisplayText());

        int statusColor;
        int statusBgColor;
        switch (request.getStatus()) {
            case MusicRequest.STATUS_ACCEPTED:
                statusColor = R.color.status_accepted;
                statusBgColor = R.color.status_accepted_bg;
                break;
            case MusicRequest.STATUS_REJECTED:
                statusColor = R.color.status_rejected;
                statusBgColor = R.color.status_rejected_bg;
                break;
            case MusicRequest.STATUS_LATER_5_15:
            case MusicRequest.STATUS_LATER_15_30:
            case MusicRequest.STATUS_LATER_30_PLUS:
                statusColor = R.color.status_later;
                statusBgColor = R.color.status_later_bg;
                break;
            case MusicRequest.STATUS_PLAYED:
                statusColor = R.color.status_played;
                statusBgColor = R.color.status_played_bg;
                break;
            default:
                statusColor = R.color.status_pending;
                statusBgColor = R.color.status_pending_bg;
        }

        holder.tvStatus.setTextColor(ContextCompat.getColor(context, statusColor));
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, statusColor));

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTrackName;
        TextView tvArtistName;
        TextView tvRequesterName;
        TextView tvTime;
        TextView tvStatus;
        View statusIndicator;
        ImageView ivAlbum;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvRequesterName = itemView.findViewById(R.id.tvRequesterName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);
        }
    }
}
