package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.CompensationRequest;
import com.deliveryclub.helpbot.repository.CompensationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompensationRequestService {

    @Autowired
    private CompensationRequestRepository compensationRequestRepository;

    // Сохранить запрос на компенсацию
    public void saveRequest(CompensationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CompensationRequest cannot be null");
        }
        compensationRequestRepository.save(request);
    }

    // Получить запросы пользователя
    public List<CompensationRequest> getRequestsByUserId(long chatId) {
        String userId = String.valueOf(chatId);
        return compensationRequestRepository.findByUserId(userId);
    }

    // Получить все запросы на рассмотрении
    public List<CompensationRequest> getPendingRequests() {
        return compensationRequestRepository.findByStatus("PENDING");
    }

    // Утвердить запрос на компенсацию
    public void approveCompensation(String requestId) {
        Optional<CompensationRequest> requestOpt = compensationRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            CompensationRequest request = requestOpt.get();
            request.setStatus("APPROVED");
            compensationRequestRepository.save(request);
        }
    }

    // Отклонить запрос на компенсацию
    public void rejectCompensation(String requestId) {
        Optional<CompensationRequest> requestOpt = compensationRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            CompensationRequest request = requestOpt.get();
            request.setStatus("REJECTED");
            compensationRequestRepository.save(request);
        }
    }
}