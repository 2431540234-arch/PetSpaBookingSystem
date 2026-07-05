package com.petspa.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notif_settings")
public class NotificationSetting {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "booking_new")
    private Boolean bookingNew;

    @Column(name = "booking_confirmed")
    private Boolean bookingConfirmed;

    @Column(name = "booking_rescheduled")
    private Boolean bookingRescheduled;

    @Column(name = "booking_cancelled")
    private Boolean bookingCancelled;

    @Column(name = "payment_success")
    private Boolean paymentSuccess;

    @Column(name = "payment_failed")
    private Boolean paymentFailed;

    @Column(name = "service_in_progress")
    private Boolean serviceInProgress;

    @Column(name = "service_completed")
    private Boolean serviceCompleted;

    @Column(name = "channel_push")
    private Boolean channelPush;

    @Column(name = "channel_email")
    private Boolean channelEmail;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getBookingNew() {
        return bookingNew;
    }

    public void setBookingNew(Boolean bookingNew) {
        this.bookingNew = bookingNew;
    }

    public Boolean getBookingConfirmed() {
        return bookingConfirmed;
    }

    public void setBookingConfirmed(Boolean bookingConfirmed) {
        this.bookingConfirmed = bookingConfirmed;
    }

    public Boolean getBookingRescheduled() {
        return bookingRescheduled;
    }

    public void setBookingRescheduled(Boolean bookingRescheduled) {
        this.bookingRescheduled = bookingRescheduled;
    }

    public Boolean getBookingCancelled() {
        return bookingCancelled;
    }

    public void setBookingCancelled(Boolean bookingCancelled) {
        this.bookingCancelled = bookingCancelled;
    }

    public Boolean getPaymentSuccess() {
        return paymentSuccess;
    }

    public void setPaymentSuccess(Boolean paymentSuccess) {
        this.paymentSuccess = paymentSuccess;
    }

    public Boolean getPaymentFailed() {
        return paymentFailed;
    }

    public void setPaymentFailed(Boolean paymentFailed) {
        this.paymentFailed = paymentFailed;
    }

    public Boolean getServiceInProgress() {
        return serviceInProgress;
    }

    public void setServiceInProgress(Boolean serviceInProgress) {
        this.serviceInProgress = serviceInProgress;
    }

    public Boolean getServiceCompleted() {
        return serviceCompleted;
    }

    public void setServiceCompleted(Boolean serviceCompleted) {
        this.serviceCompleted = serviceCompleted;
    }

    public Boolean getChannelPush() {
        return channelPush;
    }

    public void setChannelPush(Boolean channelPush) {
        this.channelPush = channelPush;
    }

    public Boolean getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(Boolean channelEmail) {
        this.channelEmail = channelEmail;
    }
}

