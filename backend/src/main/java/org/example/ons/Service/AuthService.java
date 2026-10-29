package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.ChangePasswordDTO;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;

    public List<User> getAllUsers(){
        List<User> users = authRepository.findAll();

        if(users.isEmpty()){
            throw new ApiException("there is no users in the system");
        }
        return users;
    }

    public User getUserById(Integer id){
        User user = authRepository.findUserById(id);
        if(user==null){
            throw new ApiException("user not found");
        }
        return user;
    }

    public void activateUser(Integer user_id){
        User user = authRepository.findUserById(user_id);
        if(user==null){
            throw new ApiException("user not found");
        }

        user.setActive(true);
        authRepository.save(user);
    }

    public void deactivateUser(Integer user_id){
        User user = authRepository.findUserById(user_id);
        if(user==null){
            throw new ApiException("user not found");
        }

        user.setActive(false);
        authRepository.save(user);
    }

    public void changePassword(
            Integer userId,
            ChangePasswordDTO dto) {

        User user = authRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ApiException("Wrong current password");
        }

        if (encoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ApiException(
                    "New password must be different from the current password"
            );
        }

        user.setPassword(encoder.encode(dto.getNewPassword()));

        authRepository.save(user);
    }
}
