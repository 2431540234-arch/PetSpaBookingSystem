package com.petspa.backend.controller;

import com.petspa.backend.dto.request.StaffRequestRequest;
import com.petspa.backend.dto.response.StaffRequestResponse;
import com.petspa.backend.service.StaffRequestService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff-requests")
public class StaffRequestController {

    private final StaffRequestService staffRequestService;

    public StaffRequestController(StaffRequestService staffRequestService) {
        this.staffRequestService = staffRequestService;
    }

    @PostMapping
    public StaffRequestResponse createRequest(@Valid @RequestBody StaffRequestRequest request,
                                              Authentication authentication) {
        String staffId = authentication.getName();
        return staffRequestService.createRequest(request, staffId);
    }

    @GetMapping("/{requestId}")
    public StaffRequestResponse getRequestById(@PathVariable String requestId) {
        return staffRequestService.getRequestById(requestId);
    }

    @GetMapping("/my-requests")
    public List<StaffRequestResponse> getMyRequests(Authentication authentication) {
        String staffId = authentication.getName();
        return staffRequestService.getRequestsByStaffId(staffId);
    }

    @GetMapping("/pending")
    public List<StaffRequestResponse> getPendingRequests() {
        return staffRequestService.getPendingRequests();
    }

    @PutMapping("/{requestId}/approve")
    public StaffRequestResponse approveRequest(@PathVariable String requestId) {
        return staffRequestService.approveRequest(requestId);
    }

    @PutMapping("/{requestId}/reject")
    public StaffRequestResponse rejectRequest(@PathVariable String requestId) {
        return staffRequestService.rejectRequest(requestId);
    }

    @DeleteMapping("/{requestId}")
    public void deleteRequest(@PathVariable String requestId) {
        staffRequestService.deleteRequest(requestId);
    }
}

