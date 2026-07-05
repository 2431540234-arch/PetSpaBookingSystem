package com.petspa.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class StaffRequestRequest {

    @NotBlank
    private String type;

    private String date;

    private String reason;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

