package com.tetsworks.musicask;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.MusicRequest;
import com.tetsworks.musicask.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvTrackName;
    private TextView tvArtistName;
    private TextView tvRequesterName;
    private TextView tvRequestTime;
    private TextView tvCurrentStatus;
    private Button btnAccept;
    private Button btnReject;
    private Button btnLater5;
    private Button btnLater15;
    private Button btnLater30;
    private LinearLayout layoutLaterOptions;
    private Button btnShowLater;
    private ProgressBar progressBar;

    private String requestId;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        requestId = getIntent().getStringExtra("request_id");
        if (requestId == null) {
            Toast.makeText(this, "Pedido não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadRequestData();
        setupClickListeners();
    }

    private void initViews() {
        tvTrackName = findViewById(R.id.tvTrackName);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvRequesterName = findViewById(R.id.tvRequesterName);
        tvRequestTime = findViewById(R.id.tvRequestTime);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnLater5 = findViewById(R.id.btnLater5);
        btnLater15 = findViewById(R.id.btnLater15);
        btnLater30 = findViewById(R.id.btnLater30);
        layoutLaterOptions = findViewById(R.id.layoutLaterOptions);
        btnShowLater = findViewById(R.id.btnShowLater);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadRequestData() {
        String trackName = getIntent().getStringExtra("track_name");
        String artistName = getIntent().getStringExtra("artist_name");
        String requesterName = getIntent().getStringExtra("requester_name");
        currentStatus = getIntent().getStringExtra("status");
        long requestedAt = getIntent().getLongExtra("requested_at", System.currentTimeMillis());

        tvTrackName.setText(trackName);
        tvArtistName.setText(artistName);
        tvRequesterName.setText("Pedido por: " + requesterName);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvRequestTime.setText("Horário: " + sdf.format(new Date(requestedAt)));

        updateStatusDisplay();
    }

    private void setupClickListeners() {
        btnAccept.setOnClickListener(v -> updateStatus(MusicRequest.STATUS_ACCEPTED));
        btnReject.setOnClickListener(v -> updateStatus(MusicRequest.STATUS_REJECTED));

        btnShowLater.setOnClickListener(v -> {
            if (layoutLaterOptions.getVisibility() == View.VISIBLE) {
                layoutLaterOptions.setVisibility(View.GONE);
                btnShowLater.setText("⏳ Mais Tarde");
            } else {
                layoutLaterOptions.setVisibility(View.VISIBLE);
                btnShowLater.setText("Fechar opções");
            }
        });

        btnLater5.setOnClickListener(v -> updateStatus(MusicRequest.STATUS_LATER_5_15));
        btnLater15.setOnClickListener(v -> updateStatus(MusicRequest.STATUS_LATER_15_30));
        btnLater30.setOnClickListener(v -> updateStatus(MusicRequest.STATUS_LATER_30_PLUS));
    }

    private void updateStatus(String newStatus) {
        showLoading(true);

        Map<String, Object> body = new HashMap<>();
        body.put("status", newStatus);

        RetrofitClient.getInstance(this)
                .getApiService()
                .updateRequestStatus(requestId, body)
                .enqueue(new Callback<ApiResponse<MusicRequest>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MusicRequest>> call, Response<ApiResponse<MusicRequest>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<MusicRequest> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                currentStatus = newStatus;
                                updateStatusDisplay();
                                Toast.makeText(RequestDetailActivity.this, "Status atualizado!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                            } else {
                                Toast.makeText(RequestDetailActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RequestDetailActivity.this, "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MusicRequest>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(RequestDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatusDisplay() {
        String statusText;
        int bgColor;

        switch (currentStatus) {
            case MusicRequest.STATUS_ACCEPTED:
                statusText = "✓ Programada para tocar";
                bgColor = R.color.status_accepted_bg;
                break;
            case MusicRequest.STATUS_REJECTED:
                statusText = "✗ Não disponível";
                bgColor = R.color.status_rejected_bg;
                break;
            case MusicRequest.STATUS_LATER_5_15:
                statusText = "⏳ Agendada (5-15 min)";
                bgColor = R.color.status_later_bg;
                break;
            case MusicRequest.STATUS_LATER_15_30:
                statusText = "⏳ Agendada (15-30 min)";
                bgColor = R.color.status_later_bg;
                break;
            case MusicRequest.STATUS_LATER_30_PLUS:
                statusText = "⏳ Agendada (30+ min)";
                bgColor = R.color.status_later_bg;
                break;
            case MusicRequest.STATUS_PLAYED:
                statusText = "♪ Tocada";
                bgColor = R.color.status_played_bg;
                break;
            default:
                statusText = "Aguardando resposta";
                bgColor = R.color.status_pending_bg;
        }

        tvCurrentStatus.setText(statusText);
        tvCurrentStatus.setBackgroundResource(bgColor);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAccept.setEnabled(!show);
        btnReject.setEnabled(!show);
        btnShowLater.setEnabled(!show);
        btnLater5.setEnabled(!show);
        btnLater15.setEnabled(!show);
        btnLater30.setEnabled(!show);
    }
}
