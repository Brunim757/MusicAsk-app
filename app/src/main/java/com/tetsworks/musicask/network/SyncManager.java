package com.tetsworks.musicask.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.MusicRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncManager {
    private static final String TAG = "SyncManager";
    private static final long SYNC_INTERVAL = 30000;

    private final Context context;
    private final Handler handler;
    private final Runnable syncRunnable;
    private boolean isRunning;
    private String currentEventId;
    private SyncListener listener;

    public interface SyncListener {
        void onRequestsUpdated(List<MusicRequest> requests);
        void onSyncError(String error);
        void onSyncStatusChanged(boolean isSyncing);
    }

    public SyncManager(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.syncRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning && currentEventId != null) {
                    performSync();
                    handler.postDelayed(this, SYNC_INTERVAL);
                }
            }
        };
    }

    public void setListener(SyncListener listener) {
        this.listener = listener;
    }

    public void startSync(String eventId) {
        this.currentEventId = eventId;
        this.isRunning = true;
        handler.post(syncRunnable);
        Log.d(TAG, "Sync started for event: " + eventId);
    }

    public void stopSync() {
        this.isRunning = false;
        handler.removeCallbacks(syncRunnable);
        Log.d(TAG, "Sync stopped");
    }

    public void syncNow() {
        if (currentEventId != null) {
            performSync();
        }
    }

    private void performSync() {
        if (listener != null) {
            listener.onSyncStatusChanged(true);
        }

        RetrofitClient.getInstance(context)
                .getApiService()
                .getEventRequests(currentEventId, null)
                .enqueue(new Callback<ApiResponse<List<MusicRequest>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<MusicRequest>>> call,
                                           Response<ApiResponse<List<MusicRequest>>> response) {
                        if (listener != null) {
                            listener.onSyncStatusChanged(false);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<MusicRequest>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && listener != null) {
                                listener.onRequestsUpdated(apiResponse.getData());
                            }
                        } else {
                            if (listener != null) {
                                listener.onSyncError("Erro ao sincronizar pedidos");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<MusicRequest>>> call, Throwable t) {
                        if (listener != null) {
                            listener.onSyncStatusChanged(false);
                            listener.onSyncError("Sem conexão. Tentando novamente em 30s...");
                        }
                        Log.e(TAG, "Sync failed: " + t.getMessage());
                    }
                });
    }
}
