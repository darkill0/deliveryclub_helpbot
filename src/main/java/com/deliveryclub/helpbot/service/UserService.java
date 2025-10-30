package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.User;
import com.deliveryclub.helpbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void createNewUser(String username, Long telegramId, String role, String department, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setTelegramId(telegramId);
        newUser.setRole(role);
        newUser.setDepartment(department);
        newUser.setPasswordHash(password);
        newUser.setCreatedAt(new Date());
        newUser.setLastLogin(new Date());

        // Сохраняем пользователя в базе данных
        userRepository.save(newUser);
    }
}
