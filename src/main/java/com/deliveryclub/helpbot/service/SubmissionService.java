package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.Submission;
import com.deliveryclub.helpbot.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    // Получение всех заявок с статусом "pending"
    public List<Submission> getPendingSubmissions() {
        return submissionRepository.findByStatus("pending");
    }

    // Получение всех заявок (независимо от статуса)
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    // Получение заявки по её ID
    public Submission getSubmissionById(String submissionId) {
        return submissionRepository.findById(submissionId).orElse(null);
    }

    // Сохранение заявки
    public void saveSubmission(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null");
        }
        if (submission.getStatus() == null) {
            submission.setStatus("pending"); // Устанавливаем статус по умолчанию
        }
        submissionRepository.save(submission);
    }

    // Утверждение заявки
    public void approveSubmission(String submissionId) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setStatus("approved");
            submissionRepository.save(submission);
        }
    }

    // Отклонение заявки
    public void rejectSubmission(String submissionId) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setStatus("rejected");
            submissionRepository.save(submission);
        }
    }

    // Подсчёт количества заявок с определённым статусом
    public long countSubmissionsByStatus(String status) {
        return submissionRepository.countByStatus(status);
    }

    public Submission findByTitleAndSubmittedBy(String chosenTitle, String userIdByChatId) {
        return submissionRepository.findByTitleAndSubmittedBy(chosenTitle, userIdByChatId);
    }

    public List<Submission> findBySubmittedBy(String userIdByChatId) {
        return submissionRepository.findBySubmittedBy(userIdByChatId);
    }

    public List<Submission> getSubmissionsByUser(long chatId) {
        String userId = String.valueOf(chatId);
        return submissionRepository.findBySubmittedBy(userId);
    }

    public Submission findById(String submissionId) {
        return submissionRepository.findById(submissionId).orElse(null);
    }
}