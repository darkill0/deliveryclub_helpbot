package com.deliveryclub.helpbot.bot;

import com.deliveryclub.helpbot.bot.handlers.AdminHandler;
import com.deliveryclub.helpbot.bot.handlers.UserHandler;
import com.deliveryclub.helpbot.bot.keyboards.KeyboardService;
import com.deliveryclub.helpbot.models.*;
import com.deliveryclub.helpbot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class EmployeeHelpBot extends TelegramLongPollingBot {
    @Autowired
    private UserStateService userStateService;
    @Autowired
    KeyboardService keyboardService;
    @Autowired
    @Lazy
    AdminHandler adminHandler;
    @Autowired
    @Lazy
    UserHandler userHandler;
    @Autowired
    UserService userService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CompensationRequestService compensationRequestService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ShiftService shiftService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private SickLeaveService sickLeaveService;

    @Autowired
    private UserRequestService userRequestService;

    @Autowired
    private CallRuleService callRuleService;

    @Autowired
    private LinkService linkService;

    @Override
    public String getBotUsername() {
        return "helpdelivery_bot";
    }

    @Override
    public String getBotToken() {
        return "8079512753:AAG-YtqI2pabqeqG17zVFebDtBcwrMhd-RQ";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            Document userFile = update.getMessage().getDocument();
            long chatId = update.getMessage().getChatId();
            String role = getUserRole(update.getMessage().getFrom().getId()); // Метод для получения роли пользователя
            String userState = userStateService.getUserState(chatId);
            if(userMessage != null) {
                if(userService.findUserByTelegramId(update.getMessage().getFrom().getId()).isPresent() && userMessage.equalsIgnoreCase("/start")){
                    String responseText = switch (role) {
                        case "admin" -> "Добро пожаловать, администратор! Вы можете управлять пользователями и системой.";
                        case "moderator" -> "Добро пожаловать, модератор! Вы можете модерировать вопросы.";
                        case "user" -> "Добро пожаловать! Чем я могу помочь?";
                        default -> "Ваша роль не определена. Обратитесь к администратору.";
                    };
                    if(role.equals("user")){
                        sendUserKeyboard(chatId, responseText);
                    }
                    if(role.equals("admin")){
                        sendAdminKeyboard(chatId, responseText);
                    }
                    if(role.equals("moderator")){
                        sendModeratorKeyboard(chatId, responseText);
                    }
                }else if(userService.findUserByTelegramId(update.getMessage().getFrom().getId()).isEmpty()){
                    sendMessage(chatId, "Вы не авторизованы!");
                }
                if (userMessage.equalsIgnoreCase("/dictionary")) {
                    sendCategorySelection(chatId);
                    return;
                }

                if (role.equals("user")) {
                    if (userMessage.equals("📜 Все правила")) {
                        List<Question> allQuestions = questionService.getAllQuestions();
                        if (allQuestions.isEmpty()) {
                            sendMessage(chatId, "В базе данных пока нет правил.");
                        } else {
                            StringBuilder rulesText = new StringBuilder("📜 Все правила:\n\n");
                            for (Question question : allQuestions) {
                                rulesText.append(String.format(
                                        "Вопрос: %s\nОтвет: %s\nКатегория: %s\nID: %s\n\n",
                                        question.getQuestion(), question.getAnswer(), question.getCategory(), question.getId()
                                ));
                            }
                            sendMessage(chatId, rulesText.toString());
                        }
                        return;
                    }

                    if (userMessage.equals("❓ Команды")) {
                        String commandsText = """
                                ❓ Доступные команды и их использование:
                                                            
                                /start - Начать взаимодействие с ботом
                                /dictionary - Показать категории словаря
                                /links - Показать полезные ссылки
                                /callrules - Показать правила звонков
                                /search <запрос> - Поиск по базе знаний (например, /search ЛПР)
                                                            
                                Кнопки:
                                💼 Практики - Показать практики
                                🏥 Больничные - Загрузить больничный
                                📄 Мои предложения - Показать ваши предложения
                                📩 Загрузить файл - Загрузить файл
                                📄 Создать предложение - Создать новое предложение
                                ⏳ Взять отгул / Форс-мажор - Запросить отгул или форс-мажор
                                💸 Запрос компенсации - Запросить компенсацию
                                📅 Мои смены - Показать ваши смены
                                📜 Все правила - Показать все правила из базы
                                ❓ Команды - Показать это сообщение
                                """;
                        sendMessage(chatId, commandsText);
                        return;
                    }
                }

                if (role.equals("moderator")) {
                    // Обработка "Назначить задачу"
                    if (userMessage.equals("📋 Назначить задачу")) {
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText("Введите chatId пользователя или выберите из списка:");
                        message.setReplyMarkup(keyboardService.getUserSelectionKeyboard());
                        sendMessageWithInline(chatId, message);
                        return;
                    }

                    // Обработка ввода задачи после выбора пользователя
                    if (userState != null && userState.startsWith("awaiting_task_")) {
                        String assignedTo = userState.replace("awaiting_task_", "");
                        try {
                            // Ожидаем ввод в формате: "описание, дата время" (например, "Позвонить в ресторан X, 2025-03-18 14:00")
                            String[] parts = userMessage.split(", ");
                            if (parts.length != 2) {
                                throw new IllegalArgumentException("Неверный формат");
                            }
                            String description = parts[0];
                            String deadlineStr = parts[1];
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date deadline = sdf.parse(deadlineStr);

                            Task task = new Task();
                            task.setAssignedTo(assignedTo);
                            task.setDescription(description);
                            task.setDeadline(deadline);
                            task.setAssignedBy(String.valueOf(chatId));

                            taskService.saveTask(task);
                            sendMessage(chatId, "Задача назначена пользователю " + assignedTo + ".");
                            // Уведомление пользователю
                            long userChatId = Long.parseLong(assignedTo);
                            sendMessage(userChatId, "📋 Новая задача: " + description + ". Дедлайн: " + deadlineStr);
                            userStateService.clearUserState(chatId);
                        } catch (Exception e) {
                            sendMessage(chatId, "Ошибка: укажите задачу в формате 'Описание, yyyy-MM-dd HH:mm' (например, 'Позвонить в ресторан X, 2025-03-18 14:00')");
                        }
                        return;
                    }
                }

                if (role.equals("user") && userMessage.equals("💸 Запрос компенсации")) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Выберите тип компенсации:");
                    message.setReplyMarkup(keyboardService.getCompensationTypeKeyboard());
                    sendMessageWithInline(chatId, message);
                    return;
                }

                // Обработка ввода суммы и описания после выбора типа
                if (userState != null && userState.startsWith("awaiting_compensation_")) {
                    String type = userState.replace("awaiting_compensation_", "");
                    Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*(.*)");
                    Matcher matcher = pattern.matcher(userMessage);
                    if (matcher.find()) {
                        double amount = Double.parseDouble(matcher.group(1));
                        String description = matcher.group(2).trim();
                        CompensationRequest request = new CompensationRequest(String.valueOf(chatId), type, amount, description);
                        compensationRequestService.saveRequest(request);
                        sendMessage(chatId, "Ваш запрос на компенсацию отправлен на рассмотрение.");
                        userStateService.clearUserState(chatId);
                    } else {
                        sendMessage(chatId, "Пожалуйста, укажите сумму и описание в формате: '500 Описание'");
                    }
                    return;
                }

                if (role.equals("user") && userMessage.equals("⏳ Взять отгул / Форс-мажор")) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Выберите тип запроса:");
                    message.setReplyMarkup(keyboardService.getRequestTypeKeyboard());
                    sendMessageWithInline(chatId, message);
                    return;
                }

                // Обработка ввода описания после выбора типа запроса
                if (userState != null && userState.startsWith("awaiting_request_")) {
                    String requestType = userState.replace("awaiting_request_", "");
                    UserRequest request = new UserRequest(String.valueOf(chatId), requestType, userMessage);
                    userRequestService.saveRequest(request);
                    sendMessage(chatId, "Ваш запрос на " + (requestType.equals("DAY_OFF") ? "отгул" : "форс-мажор") + " отправлен на рассмотрение.");
                    userStateService.clearUserState(chatId);
                    return;
                }

                if (userMessage.equalsIgnoreCase("/links")) {
                    sendLinkCategorySelection(chatId);
                    return;
                }

                if (role.equals("moderator") && userMessage.equals("🏥 Проверить больничные")) {
                    List<SickLeave> pendingSickLeaves = sickLeaveService.getPendingSickLeaves();
                    if (pendingSickLeaves.isEmpty()) {
                        sendMessage(chatId, "Нет больничных на рассмотрении.");
                    } else {
                        sendPendingSickLeaves(chatId, pendingSickLeaves);
                    }
                    return;
                }

                if (role.equals("moderator")) {
                    // Обработка "Просмотр всех предложений"
                    if (userMessage.equals("📄 Просмотр всех предложений")) {
                        List<Submission> allSubmissions = submissionService.getAllSubmissions();
                        if (allSubmissions.isEmpty()) {
                            sendMessage(chatId, "Нет загруженных предложений.");
                        } else {
                            sendAllSubmissions(chatId, allSubmissions);
                        }
                        return;
                    }
                }

                if (role.equals("moderator")) {
                    // Обработка "Проверить предложения"
                    if (userMessage.equals("🔍 Проверить предложения")) {
                        List<Submission> pendingSubmissions = submissionService.getPendingSubmissions();
                        if (pendingSubmissions.isEmpty()) {
                            sendMessage(chatId, "Нет предложений на рассмотрении.");
                        } else {
                            sendPendingSubmissions(chatId, pendingSubmissions);
                        }
                        return;
                    }

                    // Обработка "Утвердить файл"
                    if (userMessage.equals("✔️ Утвердить файл")) {
                        List<FileResource> pendingFiles = fileService.getPendingFiles();
                        if (pendingFiles.isEmpty()) {
                            sendMessage(chatId, "Нет файлов для утверждения.");
                        } else {
                            userStateService.setUserState(chatId, "awaiting_file_approve");
                            sendMessage(chatId, "Введите ID файла для утверждения:");
                        }
                        return;
                    }

                    // Обработка "Отклонить файл"
                    if (userMessage.equals("❌ Отклонить файл")) {
                        List<FileResource> pendingFiles = fileService.getPendingFiles();
                        if (pendingFiles.isEmpty()) {
                            sendMessage(chatId, "Нет файлов для отклонения.");
                        } else {
                            userStateService.setUserState(chatId, "awaiting_file_reject");
                            sendMessage(chatId, "Введите ID файла для отклонения:");
                        }
                        return;
                    }



                    // Обработка ввода ID файла для утверждения
                    if (userState != null && userState.equals("awaiting_file_approve")) {
                        String fileId = userMessage.trim();
                        Optional<FileResource> fileOpt = fileService.getFileById(fileId);
                        if (fileOpt.isPresent() && "PENDING".equals(fileOpt.get().getStatus())) {
                            FileResource file = fileOpt.get();
                            fileService.approveFile(fileId);
                            sendMessage(chatId, "Файл с ID " + fileId + " утвержден.");
                            long userChatId = Long.parseLong(file.getUploadedBy());
                            sendMessage(userChatId, "Ваш файл '" + file.getTitle() + "' утвержден модератором.");
                        } else {
                            sendMessage(chatId, "Файл с ID " + fileId + " не найден или уже обработан.");
                        }
                        userStateService.clearUserState(chatId);
                        return;
                    }

                    // Обработка ввода ID файла для отклонения
                    if (userState != null && userState.equals("awaiting_file_reject")) {
                        String fileId = userMessage.trim();
                        Optional<FileResource> fileOpt = fileService.getFileById(fileId);
                        if (fileOpt.isPresent() && "PENDING".equals(fileOpt.get().getStatus())) {
                            FileResource file = fileOpt.get();
                            fileService.rejectFile(fileId);
                            sendMessage(chatId, "Файл с ID " + fileId + " отклонен.");
                            long userChatId = Long.parseLong(file.getUploadedBy());
                            sendMessage(userChatId, "Ваш файл '" + file.getTitle() + "' отклонен модератором.");
                        } else {
                            sendMessage(chatId, "Файл с ID " + fileId + " не найден или уже обработан.");
                        }
                        userStateService.clearUserState(chatId);
                        return;
                    }
                }

                if (role.equals("moderator") && userMessage.equals("📅 Добавить смену")) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Выберите пользователя для добавления смены:");
                    message.setReplyMarkup(keyboardService.getUserSelectionKeyboard());
                    sendMessageWithInline(chatId, message);
                    return;
                }

                // Обработка ввода данных о смене после выбора пользователя
                if (role.equals("moderator") && userState != null && userState.startsWith("awaiting_shift_")) {
                    String userId = userState.replace("awaiting_shift_", "");
                    try {
                        // Ожидаем ввод в формате: "дата начало конец" (например, "2025-03-20 09:00 17:00")
                        String[] parts = userMessage.split(" ");
                        if (parts.length != 3) {
                            throw new IllegalArgumentException("Неверный формат");
                        }
                        String date = parts[0];
                        String startTime = parts[1];
                        String endTime = parts[2];

                        Shift shift = new Shift();
                        shift.setUserId(userId);
                        shift.setDate(date);
                        shift.setStartTime(startTime);
                        shift.setEndTime(endTime);

                        shiftService.saveShift(shift);
                        sendMessage(chatId, "Смена для пользователя " + userId + " добавлена: " + date + " " + startTime + "–" + endTime);
                        // Уведомление пользователю
                        long userChatId = Long.parseLong(userId);
                        sendMessage(userChatId, "Вам назначена смена: " + date + " с " + startTime + " до " + endTime);
                        userStateService.clearUserState(chatId);
                    } catch (Exception e) {
                        sendMessage(chatId, "Ошибка: укажите данные в формате '2025-03-20 09:00 17:00'");
                    }
                    return;
                }

                if (role.equals("moderator") && userMessage.equals("💸 Проверить компенсации")) {
                    List<CompensationRequest> pendingCompensations = compensationRequestService.getPendingRequests();
                    if (pendingCompensations.isEmpty()) {
                        sendMessage(chatId, "Нет запросов на компенсацию на рассмотрении.");
                    } else {
                        sendPendingCompensations(chatId, pendingCompensations);
                    }
                    return;
                }

                if (role.equals("moderator") && userMessage.equals("⏳ Проверить отгулы/форс-мажоры")) {
                    List<UserRequest> pendingRequests = userRequestService.getPendingRequests();
                    if (pendingRequests.isEmpty()) {
                        sendMessage(chatId, "Нет отгулов или форс-мажоров на рассмотрении.");
                    } else {
                        sendPendingUserRequests(chatId, pendingRequests);
                    }
                    return;
                }



                if (userMessage.toLowerCase().startsWith("/search")) {
                    String query = userMessage.replace("/search", "").trim();
                    if (query.isEmpty()) {
                        sendMessage(chatId, "Пожалуйста, укажите запрос для поиска. Пример: /search ЛПР");
                        return;
                    }

                    List<SearchResult> results = searchService.search(query);
                    if (results.isEmpty()) {
                        sendMessage(chatId, "По запросу '" + query + "' ничего не найдено.");
                    } else {
                        sendSearchResults(chatId, query, results);
                    }
                    return;
                }

                if (role.equals("user") && userMessage.equals("📅 Мои смены")) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Выберите период для просмотра расписания:");
                    message.setReplyMarkup(keyboardService.getShiftPeriodKeyboard());
                    sendMessageWithInline(chatId, message);
                    return;
                }

                if (userMessage.equalsIgnoreCase("/callrules")) {
                    sendCallRuleCategorySelection(chatId);
                    return;
                }

                // Обработка ввода термина после выбора категории
                if (userStateService.getUserState(chatId) != null && userStateService.getUserState(chatId).startsWith("awaiting_term_")) {
                    String category = userStateService.getUserState(chatId).replace("awaiting_term_", "");
                    String term = userMessage.trim();
                    String description = dictionaryService.getTermDescription(category, term);
                    sendMessage(chatId, description);
                    userStateService.clearUserState(chatId); // Сбрасываем состояние
                    return;
                }
                if(role.equals("admin")){
                    System.out.println("testadmin");
                    adminHandler.handlerAdminMessage(update, userMessage, chatId);

                }
                else{
                    userHandler.handlerUserMessage(update, userMessage, chatId);

                }
            }





        }
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String role = getUserRole(chatId);

            if (role.equals("moderator")) {
                if (callbackData.startsWith("download_")) {
                    String submissionId = callbackData.replace("download_", "");
                    Submission submission = submissionService.getSubmissionById(submissionId);
                    if (submission != null && submission.getFileUrl() != null && !submission.getFileUrl().isEmpty()) {
                        File file = new File(submission.getFileUrl());
                        if (file.exists()) {
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(String.valueOf(chatId));
                            sendDocument.setDocument(new org.telegram.telegrambots.meta.api.objects.InputFile(file));
                            sendDocument.setCaption("Файл из предложения: " + submission.getTitle());
                            try {
                                execute(sendDocument);
                            } catch (TelegramApiException e) {
                                sendMessage(chatId, "Ошибка при отправке файла: " + e.getMessage());
                            }
                        } else {
                            sendMessage(chatId, "Ошибка: файл не найден на сервере.");
                        }
                    } else {
                        sendMessage(chatId, "Ошибка: файл или предложение не найдены.");
                    }
                }

                // Обработка выбора пользователя для назначения задачи
                if (callbackData.startsWith("assign_task_user_")) {
                    String userId = callbackData.replace("assign_task_user_", "");
                    userStateService.setUserState(chatId, "awaiting_task_" + userId);
                    sendMessage(chatId, "Введите задачу и дедлайн для пользователя " + userId + " (например, 'Позвонить в ресторан X, 2025-03-18 14:00'):");
                }
            }

            if (role.equals("moderator") && callbackData.startsWith("select_user_")) {
                String userId = callbackData.replace("select_user_", "");
                userStateService.setUserState(chatId, "awaiting_shift_" + userId);
                sendMessage(chatId, "Введите данные смены для пользователя " + userId + " в формате: 'дата начало конец' (например, '2025-03-20 09:00 17:00')");
            }

            if (role.equals("moderator")) {

                if (callbackData.startsWith("approve_submission_")) {
                    String submissionId = callbackData.replace("approve_submission_", "");
                    Submission submission = submissionService.getSubmissionById(submissionId);
                    if (submission != null) {
                        submissionService.approveSubmission(submissionId);
                        sendMessage(chatId, "Предложение с ID " + submissionId + " утверждено.");
                        long userChatId = Long.parseLong(submission.getSubmittedBy());
                        sendMessage(userChatId, "Ваше предложение '" + submission.getTitle() + "' утверждено модератором.");
                    }
                }

                // Обработка отклонения предложения
                if (callbackData.startsWith("reject_submission_")) {
                    String submissionId = callbackData.replace("reject_submission_", "");
                    Submission submission = submissionService.getSubmissionById(submissionId);
                    if (submission != null) {
                        submissionService.rejectSubmission(submissionId);
                        sendMessage(chatId, "Предложение с ID " + submissionId + " отклонено.");
                        long userChatId = Long.parseLong(submission.getSubmittedBy());
                        sendMessage(userChatId, "Ваше предложение '" + submission.getTitle() + "' отклонено модератором.");
                    }
                }

                if (callbackData.startsWith("approve_compensation_")) {
                    String requestId = callbackData.replace("approve_compensation_", "");
                    CompensationRequest request = compensationRequestService.getPendingRequests().stream()
                            .filter(r -> r.getId().equals(requestId))
                            .findFirst()
                            .orElse(null);
                    if (request != null) {
                        compensationRequestService.approveCompensation(requestId);
                        sendMessage(chatId, "Компенсация с ID " + requestId + " утверждена.");
                        // Уведомление пользователю
                        long userChatId = Long.parseLong(request.getUserId());
                        sendMessage(userChatId, String.format(
                                "Ваш запрос на компенсацию (%s, %.2f руб, %s) утвержден модератором.",
                                request.getType(), request.getAmount(), request.getDescription()
                        ));
                    }
                } else if (callbackData.startsWith("reject_compensation_")) {
                    String requestId = callbackData.replace("reject_compensation_", "");
                    CompensationRequest request = compensationRequestService.getPendingRequests().stream()
                            .filter(r -> r.getId().equals(requestId))
                            .findFirst()
                            .orElse(null);
                    if (request != null) {
                        compensationRequestService.rejectCompensation(requestId);
                        sendMessage(chatId, "Компенсация с ID " + requestId + " отклонена.");
                        // Уведомление пользователю
                        long userChatId = Long.parseLong(request.getUserId());
                        sendMessage(userChatId, String.format(
                                "Ваш запрос на компенсацию (%s, %.2f руб, %s) отклонен модератором.",
                                request.getType(), request.getAmount(), request.getDescription()
                        ));
                    }
                }

                if (callbackData.startsWith("approve_sickleave_")) {
                    String sickLeaveId = callbackData.replace("approve_sickleave_", "");
                    sickLeaveService.approveSickLeave(sickLeaveId);
                    sendMessage(chatId, "Больничный с ID " + sickLeaveId + " утвержден.");
                } else if (callbackData.startsWith("reject_sickleave_")) {
                    String sickLeaveId = callbackData.replace("reject_sickleave_", "");
                    sickLeaveService.rejectSickLeave(sickLeaveId);
                    sendMessage(chatId, "Больничный с ID " + sickLeaveId + " отклонен.");
                }

                if (callbackData.startsWith("approve_request_")) {
                    String requestId = callbackData.replace("approve_request_", "");
                    UserRequest request = userRequestService.getPendingRequests().stream()
                            .filter(r -> r.getId().equals(requestId))
                            .findFirst()
                            .orElse(null);
                    if (request != null) {
                        userRequestService.approveRequest(requestId);
                        sendMessage(chatId, "Запрос с ID " + requestId + " утвержден.");
                        // Уведомление пользователю
                        long userChatId = Long.parseLong(request.getUserId());
                        String requestType = request.getType().equals("DAY_OFF") ? "отгул" : "форс-мажор";
                        sendMessage(userChatId, "Ваш запрос на " + requestType + " (" + request.getDescription() + ") утвержден модератором.");
                    }
                } else if (callbackData.startsWith("reject_request_")) {
                    String requestId = callbackData.replace("reject_request_", "");
                    UserRequest request = userRequestService.getPendingRequests().stream()
                            .filter(r -> r.getId().equals(requestId))
                            .findFirst()
                            .orElse(null);
                    if (request != null) {
                        userRequestService.rejectRequest(requestId);
                        sendMessage(chatId, "Запрос с ID " + requestId + " отклонен.");
                        // Уведомление пользователю
                        long userChatId = Long.parseLong(request.getUserId());
                        String requestType = request.getType().equals("DAY_OFF") ? "отгул" : "форс-мажор";
                        sendMessage(userChatId, "Ваш запрос на " + requestType + " (" + request.getDescription() + ") отклонен модератором.");
                    }
                }
            }

            if (role.equals("user")) {

                if (callbackData.equals("compensation_overtime")) {
                    userStateService.setUserState(chatId, "awaiting_compensation_OVERTIME");
                    sendMessage(chatId, "Укажите сумму и описание (например, '500 Переработка 2 часа'):");
                } else if (callbackData.equals("compensation_transport")) {
                    userStateService.setUserState(chatId, "awaiting_compensation_TRANSPORT");
                    sendMessage(chatId, "Укажите сумму и описание (например, '300 Такси до дома'):");
                } else if (callbackData.equals("compensation_other")) {
                    userStateService.setUserState(chatId, "awaiting_compensation_OTHER");
                    sendMessage(chatId, "Укажите сумму и описание (например, '1000 Дополнительные расходы'):");
                }

                if (callbackData.equals("request_day_off")) {
                    userStateService.setUserState(chatId, "awaiting_request_DAY_OFF");
                    sendMessage(chatId, "Опишите причину отгула:");
                } else if (callbackData.equals("request_force_majeure")) {
                    userStateService.setUserState(chatId, "awaiting_request_FORCE_MAJEURE");
                    sendMessage(chatId, "Опишите критическую ситуацию (форс-мажор):");
                }

                if (callbackData.equals("shift_today")) {
                    sendShiftForDay(chatId);
                } else if (callbackData.equals("shift_week")) {
                    sendShiftsForWeek(chatId);
                }
            }

            if (callbackData.startsWith("category_")) {
                String category = callbackData.replace("category_", "");
                userStateService.setUserState(chatId, "awaiting_term_" + category);
                sendMessage(chatId, "Введите термин для категории '" + category + "':");
            }


            if (callbackData.startsWith("link_category_")) {
                String categoryName = callbackData.replace("link_category_", "");
                sendLinksForCategory(chatId, categoryName);
            }

            if (callbackData.startsWith("callrule_category_")) {
                String categoryName = callbackData.replace("callrule_category_", "");
                sendRulesForCategory(chatId, categoryName);
            }

            if(role.equals("user")){
                System.out.println("asdasd");
                userHandler.handlerUserMessage(update, String.valueOf(update.getMessage()), chatId);
            }

            if(role.equals("admin")){
                System.out.println("whitepower");
                adminHandler.handlerAdminMessage(update, String.valueOf(update.getMessage()), chatId);

            }
        }

        if (update.getMessage().hasDocument()) {
            // Получение chatId из объекта Message
            long chatId = update.getMessage().getChatId();

            // Передача chatId и других данных в обработчик
            userHandler.handlerUserMessage(update, "", chatId);
        }

    }

    private void sendShiftForDay(long chatId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Shift> shifts = shiftService.getShiftsByUserAndDate(chatId, today);
        if (shifts.isEmpty()) {
            sendMessage(chatId, "На сегодня у вас нет запланированных смен.");
        } else {
            StringBuilder response = new StringBuilder("📅 Ваши смены на сегодня (" + today + "):\n");
            for (Shift shift : shifts) {
                response.append("- ").append(shift.getStartTime()).append("–").append(shift.getEndTime()).append("\n");
            }
            sendMessage(chatId, response.toString());
        }
    }

    // Отправка расписания за неделю
    private void sendShiftsForWeek(long chatId) {
        List<Shift> shifts = shiftService.getShiftsByUserForWeek(chatId);
        if (shifts.isEmpty()) {
            sendMessage(chatId, "На эту неделю у вас нет запланированных смен.");
        } else {
            // Получаем текущую дату и конец текущей недели
            LocalDate today = LocalDate.now();
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Фильтруем смены: только с текущего дня и до конца недели
            List<Shift> filteredShifts = shifts.stream()
                    .filter(shift -> {
                        LocalDate shiftDate = LocalDate.parse(shift.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                        return !shiftDate.isBefore(today) && !shiftDate.isAfter(endOfWeek);
                    })
                    .collect(Collectors.toList());

            if (filteredShifts.isEmpty()) {
                sendMessage(chatId, "На оставшуюся часть этой недели у вас нет запланированных смен.");
            } else {
                // Удаляем дубликаты (на всякий случай, как обсуждалось ранее)
                Set<Shift> uniqueShifts = new LinkedHashSet<>(filteredShifts);
                StringBuilder response = new StringBuilder("📆 Ваши смены на эту неделю (с сегодня):\n");
                for (Shift shift : uniqueShifts) {
                    response.append("- ").append(shift.getDate()).append(": ")
                            .append(shift.getStartTime()).append("–").append(shift.getEndTime()).append("\n");
                }
                sendMessage(chatId, response.toString());
            }
        }
    }

    private void sendPendingFiles(long chatId, List<FileResource> files) {
        for (FileResource file : files) {
            String messageText = String.format(
                    "📎 Файл\nПользователь: %s\nНазвание: %s\nID: %s\nСтатус: %s",
                    file.getUploadedBy(), file.getTitle(), file.getId(), file.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            message.setReplyMarkup(keyboardService.getFileModerationKeyboard(file.getId()));
            sendMessageWithInline(chatId, message);
        }
    }

    // Метод для отправки списка всех файлов
    private void sendAllFiles(long chatId, List<FileResource> files) {
        StringBuilder response = new StringBuilder("📄 Все предложения (файлы):\n");
        for (FileResource file : files) {
            response.append(String.format(
                    "ID: %s | Пользователь: %s | Название: %s | Статус: %s\n",
                    file.getId(), file.getUploadedBy(), file.getTitle(), file.getStatus()
            ));
        }
        sendMessage(chatId, response.toString());
    }

    private void sendPendingSubmissions(long chatId, List<Submission> submissions) {
        for (Submission submission : submissions) {
            String messageText = String.format(
                    "📝 Предложение\nПользователь: %s\nНазвание: %s\nОписание: %s\nID: %s\nСтатус: %s",
                    submission.getSubmittedBy(), submission.getTitle(), submission.getDescription(),
                    submission.getId(), submission.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(messageText);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            if (submission.getFileUrl() != null && !submission.getFileUrl().isEmpty()) {
                InlineKeyboardButton downloadButton = InlineKeyboardButton.builder()
                        .text("📥 Скачать файл")
                        .callbackData("download_" + submission.getId())
                        .build();
                keyboard.add(List.of(downloadButton));
            }

            markup.setKeyboard(keyboard);
            if (!keyboard.isEmpty()) {
                message.setReplyMarkup(markup);
            }

            sendMessageWithInline(chatId, message);
        }
    }

    // Метод для отправки списка всех предложений
    private void sendAllSubmissions(long chatId, List<Submission> submissions) {
        for (Submission submission : submissions) {
            String messageText = String.format(
                    "📝 Предложение\nПользователь: %s\nНазвание: %s\nОписание: %s\nID: %s\nСтатус: %s",
                    submission.getSubmittedBy(), submission.getTitle(), submission.getDescription(),
                    submission.getId(), submission.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            if ("pending".equals(submission.getStatus())) {
                message.setReplyMarkup(keyboardService.getSubmissionModerationKeyboard(submission.getId()));
            }
            sendMessageWithInline(chatId, message);
        }
    }

    private void sendPendingSickLeaves(long chatId, List<SickLeave> sickLeaves) {
        for (SickLeave sickLeave : sickLeaves) {
            String messageText = String.format(
                    "📅 Больничный\nПользователь: %s\nС: %s\nПо: %s\nСтатус: %s",
                    sickLeave.getUserId(), sickLeave.getStartDate(), sickLeave.getEndDate(), sickLeave.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            message.setReplyMarkup(keyboardService.getSickLeaveModerationKeyboard(sickLeave.getId()));
            sendMessageWithInline(chatId, message);
        }
    }

    private void sendPendingCompensations(long chatId, List<CompensationRequest> compensations) {
        for (CompensationRequest compensation : compensations) {
            String messageText = String.format(
                    "💸 Компенсация\nПользователь: %s\nТип: %s\nСумма: %.2f руб\nОписание: %s\nСтатус: %s",
                    compensation.getUserId(), compensation.getType(), compensation.getAmount(),
                    compensation.getDescription(), compensation.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            message.setReplyMarkup(keyboardService.getCompensationModerationKeyboard(compensation.getId()));
            sendMessageWithInline(chatId, message);
        }
    }

    private void sendPendingUserRequests(long chatId, List<UserRequest> requests) {
        for (UserRequest request : requests) {
            String requestType = request.getType().equals("DAY_OFF") ? "Отгул" : "Форс-мажор";
            String messageText = String.format(
                    "⏳ Запрос\nПользователь: %s\nТип: %s\nОписание: %s\nСтатус: %s",
                    request.getUserId(), requestType, request.getDescription(), request.getStatus()
            );
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            message.setReplyMarkup(keyboardService.getUserRequestModerationKeyboard(request.getId()));
            sendMessageWithInline(chatId, message);
        }
    }

    private String getUserRole(long chatId) {
        Optional<User> userOpt = userService.findUserByTelegramId(chatId);
        if (userOpt.isPresent()) {
            return userOpt.get().getRole(); // Возвращаем роль из объекта User
        } else {
            // Если пользователь не найден, создаём его с ролью "user" по умолчанию
            userService.createNewUser("User_" + chatId, chatId, "user", "unknown", null);
            return "user";
        }
    }

    private void sendSearchResults(long chatId, String query, List<SearchResult> results) {
        StringBuilder response = new StringBuilder("🔍 *Результаты поиска по запросу '" + query + "':*\n\n");
        int count = 0;

        for (SearchResult result : results) {
            if (count >= 10) { // Ограничение на 10 результатов для читаемости
                response.append("Найдено больше результатов. Уточните запрос для более точного поиска.\n");
                break;
            }
            response.append("📌 *").append(result.getSource()).append(" — ").append(result.getCategory()).append("*\n");
            if (result.getTitle() != null && !result.getTitle().isEmpty()) {
                response.append("➡️ *").append(result.getTitle()).append("*\n");
            }
            response.append(result.getDescription()).append("\n\n");
            count++;
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.toString());
        message.setParseMode("Markdown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendLinkCategorySelection(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("🔥 ВАЖНЫЕ И ПОЛЕЗНЫЕ ССЫЛКИ 🔥\nВыберите категорию:");

        List<LinkCategory> categories = linkService.getAllCategories();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (LinkCategory category : categories) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getName());
            button.setCallbackData("link_category_" + category.getName());
            row.add(button);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        sendMessageWithInline(chatId, message);
    }

    private void sendCallRuleCategorySelection(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("✨ ПРАВИЛА ДОЗВОНА ✨\nВыберите категорию:");

        List<CallRuleCategory> categories = callRuleService.getAllCategories();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (CallRuleCategory category : categories) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getName());
            button.setCallbackData("callrule_category_" + category.getName());
            row.add(button);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        sendMessageWithInline(chatId, message);
    }

    // Отправка правил для выбранной категории
    private void sendRulesForCategory(long chatId, String categoryName) {
        CallRuleCategory category = callRuleService.getCategoryByName(categoryName);
        if (category == null || category.getRules().isEmpty()) {
            sendMessage(chatId, "Правила в категории '" + categoryName + "' не найдены.");
            return;
        }

        StringBuilder response = new StringBuilder("📌 *Категория: " + categoryName + "*\n\n");
        for (CallRule rule : category.getRules()) {
            response.append("▫ ").append(rule.getDescription()).append("\n");
            if (rule.getLink() != null && !rule.getLink().isEmpty()) {
                response.append("🔗 [Подробнее](").append(rule.getLink()).append(")\n");
            }
            response.append("\n");
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.toString());
        message.setParseMode("Markdown");
        sendMessageWithInline(chatId, message);
    }

    // Отправка ссылок для выбранной категории
    private void sendLinksForCategory(long chatId, String categoryName) {
        LinkCategory category = linkService.getCategoryByName(categoryName);
        if (category == null || category.getLinks().isEmpty()) {
            sendMessage(chatId, "Ссылки в категории '" + categoryName + "' не найдены.");
            return;
        }

        StringBuilder response = new StringBuilder("📌 *Категория: " + categoryName + "*\n\n");
        for (Link link : category.getLinks()) {
            response.append("➡️ *").append(link.getName()).append("*\n")
                    .append(link.getDescription()).append("\n")
                    .append("[").append(link.getUrl()).append("](").append(link.getUrl()).append(")\n\n");
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.toString());
        message.setParseMode("Markdown");
        sendMessageWithInline(chatId, message);
    }

    private void sendCategorySelection(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите категорию:");

        List<DictionaryCategory> categories = dictionaryService.getAllCategories();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (DictionaryCategory category : categories) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getName());
            button.setCallbackData("category_" + category.getName());
            row.add(button);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        sendMessageWithInline(chatId, message);
    }



    public void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithInline(long chatId, SendMessage message) {
        try {
            execute(message);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithInline2(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendUserKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardService.getUserKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendAdminKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardService.getAdminKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendModeratorKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardService.getModeratorKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendQuestionKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Что вы хотите сделать?");
        message.setReplyMarkup(keyboardService.getQuestionManagementKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendSiclKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Что вы хотите сделать?");
        message.setReplyMarkup(keyboardService.getSickLeaveKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

