package com.tetsworks.musicask.network;

import com.tetsworks.musicask.models.ApiResponse;
import com.tetsworks.musicask.models.Event;
import com.tetsworks.musicask.models.EventStats;
import com.tetsworks.musicask.models.MusicRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/events")
    Call<ApiResponse<Event>> createEvent(@Body Map<String, Object> body);

    @GET("api/events/active")
    Call<ApiResponse<Event>> getActiveEvent();

    @PATCH("api/events/{eventId}")
    Call<ApiResponse<Event>> updateEvent(@Path("eventId") String eventId, @Body Map<String, Object> body);

    @POST("api/events/{eventId}/end")
    Call<ApiResponse<Event>> endEvent(@Path("eventId") String eventId);

    @GET("api/events")
    Call<ApiResponse<List<Event>>> getAllEvents();

    @GET("api/events/{eventId}")
    Call<ApiResponse<Event>> getEvent(@Path("eventId") String eventId);

    @GET("api/events/{eventId}/requests")
    Call<ApiResponse<List<MusicRequest>>> getEventRequests(
            @Path("eventId") String eventId,
            @Query("status") String status
    );

    @PATCH("api/requests/{requestId}")
    Call<ApiResponse<MusicRequest>> updateRequestStatus(
            @Path("requestId") String requestId,
            @Body Map<String, Object> body
    );

    @GET("api/events/{eventId}/stats")
    Call<ApiResponse<EventStats>> getEventStats(@Path("eventId") String eventId);

    @GET("api/stats/top-tracks")
    Call<ApiResponse<List<EventStats.TopTrack>>> getGlobalTopTracks();
}
