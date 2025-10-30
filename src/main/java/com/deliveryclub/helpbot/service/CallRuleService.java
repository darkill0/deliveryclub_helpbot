package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.CallRuleCategory;
import com.deliveryclub.helpbot.repository.CallRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallRuleService {

    @Autowired
    private CallRuleRepository callRuleRepository;

    // Получить все категории правил
    public List<CallRuleCategory> getAllCategories() {
        return callRuleRepository.findAll();
    }

    // Получить категорию по имени
    public CallRuleCategory getCategoryByName(String name) {
        return callRuleRepository.findByName(name);
    }

    // Добавить категорию
    public void addCategory(CallRuleCategory category) {
        callRuleRepository.save(category);
    }
}