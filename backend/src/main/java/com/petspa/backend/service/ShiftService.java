package com.petspa.backend.service;

import com.petspa.backend.dto.request.ShiftRequest;
import com.petspa.backend.dto.response.ShiftResponse;
import com.petspa.backend.entity.Shift;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public ShiftResponse createShift(ShiftRequest request, String staffId) {
        Shift shift = new Shift();
        shift.setStaffId(staffId);
        shift.setDate(request.getDate());
        shift.setType(request.getType());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setStatus("pending");

        Shift savedShift = shiftRepository.save(shift);
        return convertToResponse(savedShift);
    }

    public ShiftResponse getShiftById(String shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));
        return convertToResponse(shift);
    }

    public List<ShiftResponse> getShiftsByStaffId(String staffId) {
        List<Shift> shifts = shiftRepository.findByStaffId(staffId);
        return shifts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ShiftResponse> getShiftsByDate(String date) {
        List<Shift> shifts = shiftRepository.findByDate(date);
        return shifts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ShiftResponse> getShiftsByStaffAndDate(String staffId, String date) {
        List<Shift> shifts = shiftRepository.findByStaffIdAndDate(staffId, date);
        return shifts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public ShiftResponse updateShiftStatus(String shiftId, String status) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));

        shift.setStatus(status);
        Shift updatedShift = shiftRepository.save(shift);
        return convertToResponse(updatedShift);
    }

    public void deleteShift(String shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));
        shiftRepository.delete(shift);
    }

    private ShiftResponse convertToResponse(Shift shift) {
        ShiftResponse response = new ShiftResponse();
        response.setId(shift.getId());
        response.setStaffId(shift.getStaffId());
        response.setDate(shift.getDate());
        response.setType(shift.getType());
        response.setStartTime(shift.getStartTime());
        response.setEndTime(shift.getEndTime());
        response.setStatus(shift.getStatus());
        return response;
    }
}

