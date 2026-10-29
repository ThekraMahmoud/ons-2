package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.ChangePasswordDTO;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
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

    public void changePassword(Integer user_id, ChangePasswordDTO changePasswordDTO){
        User user = authRepository.findUserById(user_id);
        if(user==null){
            throw new ApiException("user not found");
        }

        if(!user.getPassword().equals(changePasswordDTO.getPassword())){
            throw new ApiException("wrong password");
        }


        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getPassword())) {
            throw new ApiException("New password must be different from the current password");
        }

        user.setPassword(changePasswordDTO.getNewPassword());
        authRepository.save(user);
    }

}
