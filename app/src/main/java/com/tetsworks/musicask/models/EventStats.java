package com.tetsworks.musicask.models;

import java.util.List;

public class EventStats {
    private int totalRequests;
    private int acceptedRequests;
    private int rejectedRequests;
    private int laterRequests;
    private List<TopTrack> topTracks;

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getAcceptedRequests() {
        return acceptedRequests;
    }

    public void setAcceptedRequests(int acceptedRequests) {
        this.acceptedRequests = acceptedRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(int rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public int getLaterRequests() {
        return laterRequests;
    }

    public void setLaterRequests(int laterRequests) {
        this.laterRequests = laterRequests;
    }

    public List<TopTrack> getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(List<TopTrack> topTracks) {
        this.topTracks = topTracks;
    }

    public static class TopTrack {
        private String trackName;
        private String artistName;
        private int count;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
