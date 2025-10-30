package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.Shift;
import com.deliveryclub.helpbot.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    // Получить смены пользователя за конкретный день
    public List<Shift> getShiftsByUserAndDate(long chatId, String date) {
        String userId = String.valueOf(chatId);
        return shiftRepository.findByUserIdAndDate(userId, date);
    }

    // Получить смены пользователя за неделю
    public List<Shift> getShiftsByUserForWeek(long chatId) {
        String userId = String.valueOf(chatId);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return shiftRepository.findByUserIdAndDateBetween(userId, weekStart.format(formatter), weekEnd.format(formatter));
    }

    // Сохранить смену (для тестов или админ-функций)
    public void saveShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        shiftRepository.save(shift);
    }
}