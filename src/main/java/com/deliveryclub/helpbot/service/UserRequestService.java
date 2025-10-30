package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.UserRequest;
import com.deliveryclub.helpbot.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRequestService {

    @Autowired
    private UserRequestRepository userRequestRepository;

    // Сохранить запрос
    public void saveRequest(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserRequest cannot be null");
        }
        userRequestRepository.save(request);
    }

    // Получить запросы пользователя
    public List<UserRequest> getRequestsByUserId(long chatId) {
        String userId = String.valueOf(chatId);
        return userRequestRepository.findByUserId(userId);
    }

    // Получить все запросы на рассмотрении
    public List<UserRequest> getPendingRequests() {
        return userRequestRepository.findByStatus("PENDING");
    }

    // Утвердить запрос
    public void approveRequest(String requestId) {
        Optional<UserRequest> requestOpt = userRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            UserRequest request = requestOpt.get();
            request.setStatus("APPROVED");
            userRequestRepository.save(request);
        }
    }

    // Отклонить запрос
    public void rejectRequest(String requestId) {
        Optional<UserRequest> requestOpt = userRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            UserRequest request = requestOpt.get();
            request.setStatus("REJECTED");
            userRequestRepository.save(request);
        }
    }
}