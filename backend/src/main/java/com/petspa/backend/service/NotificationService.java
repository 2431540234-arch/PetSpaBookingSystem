package com.petspa.backend.service;

import com.petspa.backend.dto.response.NotificationResponse;
import com.petspa.backend.entity.Notification;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.NotificationRepository;
import com.petspa.backend.repository.NotificationSettingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSettingRepository notificationSettingRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
    }

    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        return notifications.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToResponse(updatedNotification);
    }

    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notificationRepository.delete(notification);
    }

    // Các hàm helper để gửi notification
    public void notifyBookingCreated(String userId, String bookingId) {
        // Deprecated signature kept for compatibility - convert to Long inside sendNotification
        sendNotification(Long.valueOf(userId), "booking_new", "Đặt lịch mới", "Bạn có một lịch đặt dịch vụ mới", Long.valueOf(bookingId));
    }

    public void notifyBookingConfirmed(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "booking_confirmed", "Lịch đã xác nhận", "Lịch đặt dịch vụ của bạn đã được xác nhận", Long.valueOf(bookingId));
    }

    public void notifyBookingRescheduled(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "booking_rescheduled", "Lịch bị dời", "Lịch đặt dịch vụ của bạn đã được dời ngày giờ", Long.valueOf(bookingId));
    }

    public void notifyBookingCancelled(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "booking_cancelled", "Lịch đã hủy", "Lịch đặt dịch vụ của bạn đã bị hủy", Long.valueOf(bookingId));
    }

    public void notifyPaymentSuccess(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "payment_success", "Thanh toán thành công", "Thanh toán cho lịch đặt dịch vụ đã thành công", Long.valueOf(bookingId));
    }

    public void notifyPaymentFailed(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "payment_failed", "Thanh toán thất bại", "Thanh toán cho lịch đặt dịch vụ thất bại, vui lòng thử lại", Long.valueOf(bookingId));
    }

    public void notifyServiceInProgress(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "service_in_progress", "Dịch vụ đang diễn ra", "Dịch vụ của bạn đang được thực hiện", Long.valueOf(bookingId));
    }

    public void notifyServiceCompleted(String userId, String bookingId) {
        sendNotification(Long.valueOf(userId), "service_completed", "Dịch vụ đã hoàn thành", "Dịch vụ của bạn đã hoàn thành", Long.valueOf(bookingId));
    }

    public void notifyRequestApproved(String userId, String requestId) {
        sendNotification(Long.valueOf(userId), "request_approved", "Yêu cầu được phê duyệt", "Yêu cầu của bạn đã được phê duyệt", Long.valueOf(requestId));
    }

    public void notifyRequestRejected(String userId, String requestId) {
        sendNotification(Long.valueOf(userId), "request_rejected", "Yêu cầu bị từ chối", "Yêu cầu của bạn đã bị từ chối", Long.valueOf(requestId));
    }

    private void sendNotification(Long userId, String type, String title, String message, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedId(relatedId);
        notification.setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setTime(notification.getTime());
        response.setIsRead(notification.getIsRead());
        response.setRelatedId(notification.getRelatedId());
        return response;
    }
}

