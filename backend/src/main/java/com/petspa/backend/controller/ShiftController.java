package com.petspa.backend.controller;

import com.petspa.backend.dto.request.ShiftRequest;
import com.petspa.backend.dto.response.ShiftResponse;
import com.petspa.backend.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ShiftResponse createShift(@Valid @RequestBody ShiftRequest request, Authentication authentication) {
        String staffId = authentication.getName();
        return shiftService.createShift(request, staffId);
    }

    @GetMapping("/{shiftId}")
    public ShiftResponse getShiftById(@PathVariable String shiftId) {
        return shiftService.getShiftById(shiftId);
    }

    @GetMapping
    public List<ShiftResponse> getShifts(Authentication authentication,
                                         @RequestParam(required = false) String date) {
        String staffId = authentication.getName();

        if (date != null && !date.isEmpty()) {
            return shiftService.getShiftsByStaffAndDate(staffId, date);
        }
        return shiftService.getShiftsByStaffId(staffId);
    }

    @PutMapping("/{shiftId}/status")
    public ShiftResponse updateShiftStatus(@PathVariable String shiftId,
                                           @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return shiftService.updateShiftStatus(shiftId, status);
    }

    @DeleteMapping("/{shiftId}")
    public void deleteShift(@PathVariable String shiftId) {
        shiftService.deleteShift(shiftId);
    }
}

