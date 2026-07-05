package com.petspa.backend.controller;

import com.petspa.backend.dto.response.StaffAvailabilityResponse;
import com.petspa.backend.dto.response.StaffProfileResponse;
import com.petspa.backend.entity.StaffProfile;
import com.petspa.backend.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/me")
    public StaffProfileResponse getMyProfile(Authentication authentication) {
        String staffId = authentication.getName();
        return staffService.getStaffById(staffId);
    }

    @GetMapping("/{staffId}")
    public StaffProfileResponse getStaffById(@PathVariable String staffId) {
        return staffService.getStaffById(staffId);
    }

    @GetMapping("/available")
    public List<StaffAvailabilityResponse> getAvailableStaff(@RequestParam String date) {
        return staffService.getAvailableStaffByDate(date);
    }

    @PutMapping("/me")
    public StaffProfileResponse updateMyProfile(@RequestBody StaffProfile request, Authentication authentication) {
        String staffId = authentication.getName();
        return staffService.updateStaffProfile(staffId, request);
    }
}

