package com.tetsworks.musicask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tetsworks.musicask.adapters.EventHistoryAdapter;
import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.Event;
import com.tetsworks.musicask.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        setupRecyclerView();
        loadEvents();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new EventHistoryAdapter(this);
        adapter.setOnEventClickListener(this::openEventDetail);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadEvents() {
        showLoading(true);

        RetrofitClient.getInstance(this)
                .getApiService()
                .getAllEvents()
                .enqueue(new Callback<ApiResponse<List<Event>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Event>>> call, Response<ApiResponse<List<Event>>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Event>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                adapter.setEvents(apiResponse.getData());
                                updateEmptyState();
                            }
                        } else {
                            Toast.makeText(HistoryActivity.this, "Erro ao carregar histórico", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Event>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(HistoryActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        intent.putExtra("event_name", event.getName());
        intent.putExtra("event_code", event.getCode());
        startActivity(intent);
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
