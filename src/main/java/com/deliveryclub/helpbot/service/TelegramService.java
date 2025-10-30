package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.bot.EmployeeHelpBot;
import com.deliveryclub.helpbot.bot.keyboards.KeyboardService;
import com.deliveryclub.helpbot.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

@Service
public class TelegramService {

    private final EmployeeHelpBot telegramBot;

    @Autowired
    private KeyboardService keyboardService;

    public TelegramService(EmployeeHelpBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // Отправка текстового сообщения
    public void sendMessage(long chatId, String message) {
        telegramBot.sendMessage(chatId, message);
    }

    // Отправка клавиатуры управления вопросами
    public void sendQuestionKeyboard(long chatId) {
        telegramBot.sendQuestionKeyboard(chatId);
    }

    // Отправка сообщения с инлайн-клавиатурой
    public void sendInlineKeyboard(long chatId, SendMessage message) {
        telegramBot.sendMessageWithInline(chatId, message);
    }

    public void sendInlineKeyboard(long chatId, String text, InlineKeyboardMarkup markup) {
        telegramBot.sendMessageWithInline2(chatId, text,markup);
    }

    // Отправка клавиатуры для больничных (предполагается, что метод существует в EmployeeHelpBot)
    public void sendSilcKeyboard(long chatId) {
        telegramBot.sendSiclKeyboard(chatId);
    }

    // Получение файла по fileId
    public org.telegram.telegrambots.meta.api.objects.File getFile(String fileId) {
        try {
            return telegramBot.execute(new GetFile(fileId));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении файла: " + e.getMessage(), e);
        }
    }

    // Загрузка файла
    public void downloadFile(String filePath, File destination) {
        try {
            telegramBot.downloadFile(filePath, destination);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }
    }

    // Отправка списка вопросов с пагинацией
    public void sendQuestionsList(long chatId, List<Question> questions, int page) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (questions.isEmpty()) {
            message.setText("Список вопросов пуст.");
        } else {
            int totalPages = (int) Math.ceil((double) questions.size() / 5); // Предполагаем 5 вопросов на страницу
            message.setText("Список вопросов (страница " + (page + 1) + " из " + totalPages + "):");
            message.setReplyMarkup(keyboardService.getQuestionsListKeyboard(questions, page));
        }
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при отправке списка вопросов: " + e.getMessage(), e);
        }
    }
}