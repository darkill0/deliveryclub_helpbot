package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.SickLeave;
import com.deliveryclub.helpbot.repository.SickLeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SickLeaveService {

    @Autowired
    private SickLeaveRepository sickLeaveRepository;

    // Получить больничные по ID пользователя
    public List<SickLeave> getSickLeavesByUserId(long chatId) {
        String userId = getUserIdByChatId(chatId);
        return sickLeaveRepository.findByUserId(userId);
    }

    // Получить все больничные на рассмотрении
    public List<SickLeave> getPendingSickLeaves() {
        return sickLeaveRepository.findByStatus("PENDING");
    }

    // Сохранить больничный
    public void saveSickLeave(SickLeave sickLeave) {
        if (sickLeave == null) {
            throw new IllegalArgumentException("SickLeave cannot be null");
        }
        sickLeaveRepository.save(sickLeave);
    }

    // Утвердить больничный
    public void approveSickLeave(String sickLeaveId) {
        Optional<SickLeave> sickLeaveOpt = sickLeaveRepository.findById(sickLeaveId);
        if (sickLeaveOpt.isPresent()) {
            SickLeave sickLeave = sickLeaveOpt.get();
            sickLeave.setStatus("APPROVED");
            sickLeaveRepository.save(sickLeave);
        }
    }

    // Отклонить больничный
    public void rejectSickLeave(String sickLeaveId) {
        Optional<SickLeave> sickLeaveOpt = sickLeaveRepository.findById(sickLeaveId);
        if (sickLeaveOpt.isPresent()) {
            SickLeave sickLeave = sickLeaveOpt.get();
            sickLeave.setStatus("REJECTED");
            sickLeaveRepository.save(sickLeave);
        }
    }

    private String getUserIdByChatId(long chatId) {
        return String.valueOf(chatId); // Логика получения userId из chatId
    }
}