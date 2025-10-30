package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.LinkCategory;
import com.deliveryclub.helpbot.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    // Получить все категории ссылок
    public List<LinkCategory> getAllCategories() {
        return linkRepository.findAll();
    }

    // Получить категорию по имени
    public LinkCategory getCategoryByName(String name) {
        return linkRepository.findByName(name);
    }

    // Добавить категорию
    public void addCategory(LinkCategory category) {
        linkRepository.save(category);
    }
}