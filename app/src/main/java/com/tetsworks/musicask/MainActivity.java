package com.tetsworks.musicask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.Event;
import com.tetsworks.musicask.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layoutNoEvent;
    private LinearLayout layoutActiveEvent;
    private TextView tvEventCode;
    private TextView tvEventName;
    private TextView tvEventStatus;
    private Button btnStartEvent;
    private Button btnViewRequests;
    private Button btnEndEvent;
    private Button btnHistory;
    private FloatingActionButton fabSettings;
    private ProgressBar progressBar;

    private Event activeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActiveEvent();
    }

    private void initViews() {
        layoutNoEvent = findViewById(R.id.layoutNoEvent);
        layoutActiveEvent = findViewById(R.id.layoutActiveEvent);
        tvEventCode = findViewById(R.id.tvEventCode);
        tvEventName = findViewById(R.id.tvEventName);
        tvEventStatus = findViewById(R.id.tvEventStatus);
        btnStartEvent = findViewById(R.id.btnStartEvent);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnEndEvent = findViewById(R.id.btnEndEvent);
        btnHistory = findViewById(R.id.btnHistory);
        fabSettings = findViewById(R.id.fabSettings);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnStartEvent.setOnClickListener(v -> showCreateEventDialog());

        btnViewRequests.setOnClickListener(v -> {
            if (activeEvent != null) {
                Intent intent = new Intent(this, RequestsActivity.class);
                intent.putExtra("event_id", activeEvent.getId());
                intent.putExtra("event_code", activeEvent.getCode());
                startActivity(intent);
            }
        });

        btnEndEvent.setOnClickListener(v -> showEndEventDialog());

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        fabSettings.setOnClickListener(v -> showSettingsDialog());
    }

    private void checkActiveEvent() {
        showLoading(true);

        RetrofitClient.getInstance(this)
                .getApiService()
                .getActiveEvent()
                .enqueue(new Callback<ApiResponse<Event>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Event>> call, Response<ApiResponse<Event>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Event> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                activeEvent = apiResponse.getData();
                                showActiveEvent();
                            } else {
                                activeEvent = null;
                                showNoEvent();
                            }
                        } else {
                            activeEvent = null;
                            showNoEvent();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Event>> call, Throwable t) {
                        showLoading(false);
                        showNoEvent();
                        Toast.makeText(MainActivity.this, "Erro de conexão. Verifique o servidor.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showActiveEvent() {
        layoutNoEvent.setVisibility(View.GONE);
        layoutActiveEvent.setVisibility(View.VISIBLE);

        tvEventCode.setText(activeEvent.getCode());
        tvEventName.setText(activeEvent.getName() != null ? activeEvent.getName() : "Evento Ativo");
        tvEventStatus.setText("Evento ativo - recebendo pedidos");
    }

    private void showNoEvent() {
        layoutNoEvent.setVisibility(View.VISIBLE);
        layoutActiveEvent.setVisibility(View.GONE);
    }

    private void showCreateEventDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_event, null);
        EditText etEventName = dialogView.findViewById(R.id.etEventName);
        TextView tvGeneratedCode = dialogView.findViewById(R.id.tvGeneratedCode);

        String generatedCode = generateEventCode();
        tvGeneratedCode.setText(generatedCode);

        new AlertDialog.Builder(this)
                .setTitle("Iniciar Novo Evento")
                .setView(dialogView)
                .setPositiveButton("Iniciar", (dialog, which) -> {
                    String eventName = etEventName.getText().toString().trim();
                    if (eventName.isEmpty()) {
                        eventName = "Evento " + generatedCode;
                    }
                    createEvent(eventName, generatedCode);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showEndEventDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Encerrar Evento")
                .setMessage("Tem certeza que deseja encerrar o evento atual? Novos pedidos não serão mais aceitos.")
                .setPositiveButton("Encerrar", (dialog, which) -> endEvent())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        EditText etServerUrl = dialogView.findViewById(R.id.etServerUrl);
        etServerUrl.setText(RetrofitClient.getInstance(this).getBaseUrl());

        new AlertDialog.Builder(this)
                .setTitle("Configurações")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String newUrl = etServerUrl.getText().toString().trim();
                    if (!newUrl.isEmpty()) {
                        RetrofitClient.getInstance(this).setBaseUrl(this, newUrl);
                        Toast.makeText(this, "URL do servidor atualizada", Toast.LENGTH_SHORT).show();
                        checkActiveEvent();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String generateEventCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    private void createEvent(String name, String code) {
        showLoading(true);

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("code", code);

        RetrofitClient.getInstance(this)
                .getApiService()
                .createEvent(body)
                .enqueue(new Callback<ApiResponse<Event>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Event>> call, Response<ApiResponse<Event>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Event> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                activeEvent = apiResponse.getData();
                                showActiveEvent();
                                Toast.makeText(MainActivity.this, "Evento iniciado!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao criar evento", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Event>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void endEvent() {
        if (activeEvent == null) return;

        showLoading(true);

        RetrofitClient.getInstance(this)
                .getApiService()
                .endEvent(activeEvent.getId())
                .enqueue(new Callback<ApiResponse<Event>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Event>> call, Response<ApiResponse<Event>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            activeEvent = null;
                            showNoEvent();
                            Toast.makeText(MainActivity.this, "Evento encerrado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao encerrar evento", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Event>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
