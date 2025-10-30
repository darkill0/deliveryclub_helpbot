package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.FileResource;
import com.deliveryclub.helpbot.repository.FileResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileResourceRepository fileResourceRepository;

    // Получить все файлы на рассмотрении (PENDING)
    public List<FileResource> getPendingFiles() {
        return fileResourceRepository.findByStatus("PENDING");
    }

    // Получить все файлы (независимо от статуса)
    public List<FileResource> getAllFiles() {
        return fileResourceRepository.findAll();
    }

    // Получить файл по ID
    public Optional<FileResource> getFileById(String fileId) {
        return fileResourceRepository.findById(fileId);
    }

    // Сохранить файл
    public void saveFile(FileResource file) {
        if (file == null) {
            throw new IllegalArgumentException("FileResource cannot be null");
        }
        if (file.getStatus() == null) {
            file.setStatus("PENDING"); // Устанавливаем статус по умолчанию
        }
        fileResourceRepository.save(file);
    }

    // Утвердить файл
    public void approveFile(String fileId) {
        Optional<FileResource> fileOpt = fileResourceRepository.findById(fileId);
        if (fileOpt.isPresent()) {
            FileResource file = fileOpt.get();
            file.setStatus("APPROVED");
            fileResourceRepository.save(file);
        }
    }

    // Отклонить файл
    public void rejectFile(String fileId) {
        Optional<FileResource> fileOpt = fileResourceRepository.findById(fileId);
        if (fileOpt.isPresent()) {
            FileResource file = fileOpt.get();
            file.setStatus("REJECTED");
            fileResourceRepository.save(file);
        }
    }
}