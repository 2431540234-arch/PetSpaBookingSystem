package com.petspa.backend.service;

import com.petspa.backend.dto.response.StaffAvailabilityResponse;
import com.petspa.backend.dto.response.StaffProfileResponse;
import com.petspa.backend.entity.StaffProfile;
import com.petspa.backend.entity.Shift;
import com.petspa.backend.entity.User;
import com.petspa.backend.enums.UserRole;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.ShiftRepository;
import com.petspa.backend.repository.StaffProfileRepository;
import com.petspa.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffProfileRepository staffProfileRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public StaffService(StaffProfileRepository staffProfileRepository,
                       UserRepository userRepository,
                       ShiftRepository shiftRepository) {
        this.staffProfileRepository = staffProfileRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    public StaffProfileResponse getStaffById(String staffId) {
        StaffProfile staffProfile = staffProfileRepository.findByUserId(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + staffId));
        return convertToResponse(staffProfile);
    }

    public StaffProfileResponse updateStaffProfile(String staffId, StaffProfile request) {
        StaffProfile staffProfile = staffProfileRepository.findByUserId(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + staffId));

        if (request.getSpecialty() != null) staffProfile.setSpecialty(request.getSpecialty());
        if (request.getPosition() != null) staffProfile.setPosition(request.getPosition());
        if (request.getAvatar() != null) staffProfile.setAvatar(request.getAvatar());
        if (request.getExpertise() != null) staffProfile.setExpertise(request.getExpertise());
        if (request.getJoinDate() != null) staffProfile.setJoinDate(request.getJoinDate());
        if (request.getStatus() != null) staffProfile.setStatus(request.getStatus());

        StaffProfile updatedProfile = staffProfileRepository.save(staffProfile);
        return convertToResponse(updatedProfile);
    }

    public List<StaffAvailabilityResponse> getAvailableStaffByDate(String date) {
        // Lấy tất cả staff
        List<User> allStaff = userRepository.findByRole(UserRole.STAFF);

        // Filter staff có shift vào ngày này với status "approved"
        List<Shift> shiftsOnDate = shiftRepository.findByDate(date);
        List<String> availableStaffIds = shiftsOnDate.stream()
                .filter(shift -> "approved".equals(shift.getStatus()))
                .map(Shift::getStaffId)
                .collect(Collectors.toList());

        // Lọc staff có available shift
        return allStaff.stream()
                .filter(staff -> availableStaffIds.contains(staff.getId()))
                .map(staff -> buildAvailabilityResponse(staff, date))
                .collect(Collectors.toList());
    }

    private StaffAvailabilityResponse buildAvailabilityResponse(User user, String date) {
        StaffProfile staffProfile = staffProfileRepository.findByUserId(user.getId())
                .orElse(new StaffProfile());

        StaffAvailabilityResponse response = new StaffAvailabilityResponse();
        response.setStaffId(user.getId());
        response.setStaffName(user.getName());
        response.setSpecialty(staffProfile.getSpecialty());
        response.setPosition(staffProfile.getPosition());
        response.setAvatar(staffProfile.getAvatar());
        response.setExpertise(staffProfile.getExpertise());
        response.setStatus(staffProfile.getStatus());

        // Lấy danh sách thời gian trống của staff trong ngày
        List<String> availableSlots = new ArrayList<>();
        // TODO: Implement logic để lấy thời gian trống dựa trên booking và shifts
        response.setAvailableSlots(availableSlots);

        return response;
    }

    private StaffProfileResponse convertToResponse(StaffProfile staffProfile) {
        StaffProfileResponse response = new StaffProfileResponse();
        response.setUserId(staffProfile.getUserId());
        response.setSpecialty(staffProfile.getSpecialty());
        response.setPosition(staffProfile.getPosition());
        response.setAvatar(staffProfile.getAvatar());
        response.setExpertise(staffProfile.getExpertise());
        response.setJoinDate(staffProfile.getJoinDate());
        response.setStatus(staffProfile.getStatus());
        return response;
    }
}

