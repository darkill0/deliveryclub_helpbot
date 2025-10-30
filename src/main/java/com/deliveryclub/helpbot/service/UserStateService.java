package com.deliveryclub.helpbot.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserStateService {
    private final Map<Long, String> userStates = new HashMap<>();
    private final Map<Long, Map<String, Object>> temporaryData = new HashMap<>();

    public void setUserState(long chatId, String state) {
        userStates.put(chatId, state);
    }

    public String getUserState(long chatId) {
        return userStates.getOrDefault(chatId, "");
    }

    public void clearUserState(long chatId) {
        userStates.remove(chatId);
        temporaryData.remove(chatId);
    }

    public void saveTemporaryData(long chatId, String key, Object value) {
        temporaryData.computeIfAbsent(chatId, k -> new HashMap<>()).put(key, value);
    }

    public Object getTemporaryData(long chatId, String key) {
        return temporaryData.getOrDefault(chatId, new HashMap<>()).get(key);
    }
}