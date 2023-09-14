package com.example.geocache.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Geocache {
    private float latitude;
    private float longitude;
    private String timestamp;
    private String id;
    private String userId;

    public Geocache() {}

    public Geocache(float latitude, float longitude, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = user.getUid();
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
