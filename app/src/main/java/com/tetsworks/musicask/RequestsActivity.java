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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tetsworks.musicask.adapters.RequestAdapter;
import com.tetsworks.musicask.models.MusicRequest;
import com.tetsworks.musicask.network.SyncManager;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements SyncManager.SyncListener {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroup;
    private TextView tvEventCode;
    private TextView tvSyncStatus;
    private TextView tvEmptyState;
    private ProgressBar progressBar;

    private SyncManager syncManager;
    private String eventId;
    private String eventCode;
    private List<MusicRequest> allRequests = new ArrayList<>();
    private String currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        eventId = getIntent().getStringExtra("event_id");
        eventCode = getIntent().getStringExtra("event_code");

        if (eventId == null) {
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupChipFilters();
        setupSwipeRefresh();
        setupSyncManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncManager != null) {
            syncManager.stopSync();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        chipGroup = findViewById(R.id.chipGroup);
        tvEventCode = findViewById(R.id.tvEventCode);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);

        tvEventCode.setText("Código: " + eventCode);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new RequestAdapter(this);
        adapter.setOnRequestClickListener(this::openRequestDetail);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupChipFilters() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = null;
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                if (chip != null) {
                    String tag = (String) chip.getTag();
                    currentFilter = tag;
                }
            }
            applyFilter();
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            syncManager.syncNow();
        });

        swipeRefresh.setColorSchemeResources(
                R.color.primary,
                R.color.secondary
        );
    }

    private void setupSyncManager() {
        syncManager = new SyncManager(this);
        syncManager.setListener(this);
        syncManager.startSync(eventId);
    }

    private void openRequestDetail(MusicRequest request) {
        Intent intent = new Intent(this, RequestDetailActivity.class);
        intent.putExtra("request_id", request.getId());
        intent.putExtra("track_name", request.getTrackName());
        intent.putExtra("artist_name", request.getArtistName());
        intent.putExtra("requester_name", request.getRequesterName());
        intent.putExtra("status", request.getStatus());
        intent.putExtra("requested_at", request.getRequestedAt());
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            syncManager.syncNow();
        }
    }

    private void applyFilter() {
        adapter.filterByStatus(allRequests, currentFilter);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            
            if (currentFilter != null) {
                tvEmptyState.setText("Nenhum pedido com este filtro");
            } else {
                tvEmptyState.setText("Nenhum pedido ainda.\nAguardando pedidos...");
            }
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestsUpdated(List<MusicRequest> requests) {
        swipeRefresh.setRefreshing(false);
        this.allRequests = requests;
        applyFilter();
    }

    @Override
    public void onSyncError(String error) {
        swipeRefresh.setRefreshing(false);
        tvSyncStatus.setText(error);
        tvSyncStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSyncStatusChanged(boolean isSyncing) {
        if (!isSyncing) {
            tvSyncStatus.setVisibility(View.GONE);
        } else {
            tvSyncStatus.setText("Sincronizando...");
            tvSyncStatus.setVisibility(View.VISIBLE);
        }
    }
}
