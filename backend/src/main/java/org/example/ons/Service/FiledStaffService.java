package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.DTO.FiledStaffDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.FieldStaff;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
import org.example.ons.Repository.FieldStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiledStaffService {
    private final FieldStaffRepository fieldStaffRepository;
    private final AuthRepository authRepository;

    public void RegisterFiledStaff(FiledStaffDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole("FIELDـSTAFF");
        user.setActive(dto.isActive());

        authRepository.save(user);

        FieldStaff staff = new FieldStaff();
        staff.setUser(user);

        fieldStaffRepository.save(staff);
    }

    public List<FieldStaff> getAllStaff() {

        List<FieldStaff> staffList = fieldStaffRepository.findAll();

        if (staffList.isEmpty()) {
            throw new ApiException("there is no field staff in the system");
        }

        return staffList;
    }

    public FieldStaff getStaffById(Integer id) {

        return fieldStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("field staff not found"));
    }

    public void updateStaff(Integer id, EmployeeUpdateDTO dto) {

        FieldStaff staff = fieldStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("field staff not found"));

        User user = staff.getUser();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setActive(dto.isActive());

        authRepository.save(user);
    }

    public void deleteStaff(Integer id) {

        FieldStaff staff = fieldStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("field staff not found"));

        fieldStaffRepository.delete(staff);
        authRepository.delete(staff.getUser());
    }


    public List<FieldStaff> getAvailableFieldStaff() {
        List<FieldStaff> fieldStaffs =
                fieldStaffRepository.findAvailableStaff(
                        List.of(
                                ReportStatus.ASSIGNED,
                                ReportStatus.IN_PROGRESS
                        )
                );

        if (fieldStaffs.isEmpty()) {
            throw new ApiException("There are no available staff");
        }

        return fieldStaffs;
    }

}
