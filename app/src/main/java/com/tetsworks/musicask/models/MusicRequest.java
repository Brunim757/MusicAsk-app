package com.tetsworks.musicask.models;

public class MusicRequest {
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_LATER_5_15 = "later_5_15";
    public static final String STATUS_LATER_15_30 = "later_15_30";
    public static final String STATUS_LATER_30_PLUS = "later_30_plus";
    public static final String STATUS_PLAYED = "played";

    private String id;
    private String eventId;
    private String trackName;
    private String artistName;
    private String albumImage;
    private String spotifyUri;
    private String requesterName;
    private String status;
    private long requestedAt;
    private long respondedAt;

    public MusicRequest() {
        this.status = STATUS_PENDING;
        this.requestedAt = System.currentTimeMillis();
    }

    public MusicRequest(String id, String trackName, String artistName, String requesterName) {
        this.id = id;
        this.trackName = trackName;
        this.artistName = artistName;
        this.requesterName = requesterName;
        this.status = STATUS_PENDING;
        this.requestedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getSpotifyUri() {
        return spotifyUri;
    }

    public void setSpotifyUri(String spotifyUri) {
        this.spotifyUri = spotifyUri;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public long getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(long respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getStatusDisplayText() {
        switch (status) {
            case STATUS_PENDING:
                return "Aguardando";
            case STATUS_ACCEPTED:
                return "Programada para tocar";
            case STATUS_REJECTED:
                return "Não disponível";
            case STATUS_LATER_5_15:
                return "Mais tarde (5-15 min)";
            case STATUS_LATER_15_30:
                return "Mais tarde (15-30 min)";
            case STATUS_LATER_30_PLUS:
                return "Mais tarde (30+ min)";
            case STATUS_PLAYED:
                return "Tocada";
            default:
                return status;
        }
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isLater() {
        return STATUS_LATER_5_15.equals(status) || 
               STATUS_LATER_15_30.equals(status) || 
               STATUS_LATER_30_PLUS.equals(status);
    }
}
