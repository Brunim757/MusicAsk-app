package com.tetsworks.musicask.models;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String id;
    private String code;
    private String name;
    private boolean active;
    private long createdAt;
    private long endedAt;
    private List<String> acceptedStyles;
    private int totalRequests;

    public Event() {
        this.acceptedStyles = new ArrayList<>();
    }

    public Event(String id, String code, String name, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.active = active;
        this.createdAt = System.currentTimeMillis();
        this.acceptedStyles = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(long endedAt) {
        this.endedAt = endedAt;
    }

    public List<String> getAcceptedStyles() {
        return acceptedStyles;
    }

    public void setAcceptedStyles(List<String> acceptedStyles) {
        this.acceptedStyles = acceptedStyles;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
}
