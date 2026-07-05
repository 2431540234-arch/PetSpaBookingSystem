package com.petspa.backend.service;

import com.petspa.backend.dto.request.StaffRequestRequest;
import com.petspa.backend.dto.response.StaffRequestResponse;
import com.petspa.backend.entity.StaffRequest;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.StaffRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffRequestService {

    private final StaffRequestRepository staffRequestRepository;
    private final NotificationService notificationService;

    public StaffRequestService(StaffRequestRepository staffRequestRepository,
                               NotificationService notificationService) {
        this.staffRequestRepository = staffRequestRepository;
        this.notificationService = notificationService;
    }

    public StaffRequestResponse createRequest(StaffRequestRequest request, String staffId) {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setStaffId(staffId);
        staffRequest.setType(request.getType());
        staffRequest.setDate(request.getDate());
        staffRequest.setReason(request.getReason());
        staffRequest.setStatus("pending");

        StaffRequest savedRequest = staffRequestRepository.save(staffRequest);
        return convertToResponse(savedRequest);
    }

    public StaffRequestResponse getRequestById(String requestId) {
        StaffRequest staffRequest = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff request not found with id: " + requestId));
        return convertToResponse(staffRequest);
    }

    public List<StaffRequestResponse> getRequestsByStaffId(String staffId) {
        List<StaffRequest> requests = staffRequestRepository.findByStaffId(staffId);
        return requests.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<StaffRequestResponse> getPendingRequests() {
        List<StaffRequest> requests = staffRequestRepository.findByStatus("pending");
        return requests.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public StaffRequestResponse approveRequest(String requestId) {
        StaffRequest staffRequest = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff request not found with id: " + requestId));

        staffRequest.setStatus("approved");
        StaffRequest updatedRequest = staffRequestRepository.save(staffRequest);

        // Notify staff
        notificationService.notifyRequestApproved(staffRequest.getStaffId(), requestId);

        return convertToResponse(updatedRequest);
    }

    public StaffRequestResponse rejectRequest(String requestId) {
        StaffRequest staffRequest = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff request not found with id: " + requestId));

        staffRequest.setStatus("rejected");
        StaffRequest updatedRequest = staffRequestRepository.save(staffRequest);

        // Notify staff
        notificationService.notifyRequestRejected(staffRequest.getStaffId(), requestId);

        return convertToResponse(updatedRequest);
    }

    public void deleteRequest(String requestId) {
        StaffRequest staffRequest = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff request not found with id: " + requestId));
        staffRequestRepository.delete(staffRequest);
    }

    private StaffRequestResponse convertToResponse(StaffRequest staffRequest) {
        StaffRequestResponse response = new StaffRequestResponse();
        response.setId(staffRequest.getId());
        response.setStaffId(staffRequest.getStaffId());
        response.setType(staffRequest.getType());
        response.setDate(staffRequest.getDate());
        response.setReason(staffRequest.getReason());
        response.setStatus(staffRequest.getStatus());
        response.setCreatedAt(staffRequest.getCreatedAt());
        return response;
    }
}

