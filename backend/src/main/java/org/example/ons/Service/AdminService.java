package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
import org.example.ons.Repository.AuthorizedStaffRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class AdminService {

   private final AuthRepository authRepository;

    @PostConstruct
    public void init() {
        createDefaultAdmin();
    }

    public void createDefaultAdmin() {
        if (authRepository.findUserByEmail("admin@ons.com") != null) {
            return;
        }

        User admin = new User();

        admin.setName("System Admin");
        admin.setEmail("admin@ons.com");
        admin.setPassword("12345678");
        admin.setPhoneNumber("0500000000");
        admin.setRole("ADMIN");
        admin.setActive(true);

        authRepository.save(admin);
    }
}
