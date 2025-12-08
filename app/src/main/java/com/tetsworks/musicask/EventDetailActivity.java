package com.tetsworks.musicask;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.EventStats;
import com.tetsworks.musicask.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvEventName;
    private TextView tvEventCode;
    private TextView tvTotalRequests;
    private TextView tvAcceptedRequests;
    private TextView tvRejectedRequests;
    private TextView tvLaterRequests;
    private RecyclerView rvTopTracks;
    private ProgressBar progressBar;
    private TextView tvNoTopTracks;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getStringExtra("event_id");
        String eventName = getIntent().getStringExtra("event_name");
        String eventCode = getIntent().getStringExtra("event_code");

        if (eventId == null) {
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        tvEventName.setText(eventName != null ? eventName : "Evento");
        tvEventCode.setText("Código: " + eventCode);

        loadEventStats();
    }

    private void initViews() {
        tvEventName = findViewById(R.id.tvEventName);
        tvEventCode = findViewById(R.id.tvEventCode);
        tvTotalRequests = findViewById(R.id.tvTotalRequests);
        tvAcceptedRequests = findViewById(R.id.tvAcceptedRequests);
        tvRejectedRequests = findViewById(R.id.tvRejectedRequests);
        tvLaterRequests = findViewById(R.id.tvLaterRequests);
        rvTopTracks = findViewById(R.id.rvTopTracks);
        progressBar = findViewById(R.id.progressBar);
        tvNoTopTracks = findViewById(R.id.tvNoTopTracks);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvTopTracks.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadEventStats() {
        showLoading(true);

        RetrofitClient.getInstance(this)
                .getApiService()
                .getEventStats(eventId)
                .enqueue(new Callback<ApiResponse<EventStats>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<EventStats>> call, Response<ApiResponse<EventStats>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<EventStats> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                displayStats(apiResponse.getData());
                            }
                        } else {
                            Toast.makeText(EventDetailActivity.this, "Erro ao carregar estatísticas", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<EventStats>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(EventDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayStats(EventStats stats) {
        tvTotalRequests.setText(String.valueOf(stats.getTotalRequests()));
        tvAcceptedRequests.setText(String.valueOf(stats.getAcceptedRequests()));
        tvRejectedRequests.setText(String.valueOf(stats.getRejectedRequests()));
        tvLaterRequests.setText(String.valueOf(stats.getLaterRequests()));

        if (stats.getTopTracks() != null && !stats.getTopTracks().isEmpty()) {
            tvNoTopTracks.setVisibility(View.GONE);
            rvTopTracks.setVisibility(View.VISIBLE);

            StringBuilder topTracksText = new StringBuilder();
            int rank = 1;
            for (EventStats.TopTrack track : stats.getTopTracks()) {
                if (rank <= 10) {
                    topTracksText.append(rank)
                            .append(". ")
                            .append(track.getTrackName())
                            .append(" - ")
                            .append(track.getArtistName())
                            .append(" (")
                            .append(track.getCount())
                            .append(" pedidos)\n");
                    rank++;
                }
            }

            TextView tvTopTracksList = new TextView(this);
            tvTopTracksList.setText(topTracksText.toString().trim());
            tvTopTracksList.setTextSize(14);
            tvTopTracksList.setPadding(16, 8, 16, 8);
        } else {
            tvNoTopTracks.setVisibility(View.VISIBLE);
            rvTopTracks.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
