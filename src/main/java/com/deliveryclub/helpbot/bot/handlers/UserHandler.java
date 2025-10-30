package com.deliveryclub.helpbot.bot.handlers;

import com.deliveryclub.helpbot.models.SickLeave;
import com.deliveryclub.helpbot.models.Submission;
import com.deliveryclub.helpbot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserHandler {

    @Autowired
    UserStateService userStateService;

    @Autowired
    TelegramService telegramService;

    @Autowired
    SubmissionService submissionService;

    @Autowired
    SickLeaveService sickLeaveService;

    public void handlerUserMessage(Update update, String userMessage, long chatId) {
        String state = userStateService.getUserState(chatId);
        System.out.println("2user");
        // Сначала проверяем callback-запросы
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
            System.out.println(callbackData);
            handleCallbackQuery(callbackData, callbackChatId);
            return; // Выходим после обработки callback-запроса
        }

        // Проверяем наличие сообщения
        if (!update.hasMessage()) {
            return; // Если сообщения нет, выходим
        }

        // Сохранение последнего документа, если он есть в сообщении
        if (update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();
            userStateService.saveTemporaryData(chatId, "lastDocument", document);
            // Если состояние уже waiting_for_file_upload, сразу обрабатываем файл
            if ("waiting_for_file_upload".equals(state)) {
                handleFileUploadProcessing(chatId, update);
            }
            return; // Выходим, чтобы не обрабатывать другие состояния для сообщения с файлом
        }

        // Обработка текстовых сообщений
        if (update.getMessage().hasText()) {
            switch (state) {
                case "NEW_SICK_LEAVE_START_DATE":
                    handleStartDateInput(chatId, userMessage);
                    break;
                case "NEW_SICK_LEAVE_END_DATE":
                    handleEndDateInput(chatId, userMessage);
                    break;
                case "NEW_SICK_LEAVE_CONFIRMATION":
                    handleSickLeaveConfirmation(chatId, userMessage);
                    break;
                case "waiting_for_submission_title":
                    handleSubmissionTitle(chatId, userMessage);
                    break;
                case "waiting_for_submission_description":
                    handleSubmissionDescription(chatId, userMessage);
                    break;
                case "waiting_for_submission_confirmation":
                    handleSubmissionConfirmation(chatId, userMessage);
                    break;
                case "waiting_for_file_upload":
                    // Обрабатываем только текст "Да" для использования последнего файла
                    if (userMessage.equalsIgnoreCase("Да")) {
                        handleFileUploadProcessing(chatId, update);
                    } else {
                        telegramService.sendMessage(chatId, "Пожалуйста, отправьте файл или подтвердите использование последнего файла, отправив 'Да'.");
                    }
                    break;
                case "waiting_for_submission_choice":
                    telegramService.sendMessage(chatId, "sds");
                    break;
                default:
                    if (userMessage.equalsIgnoreCase("🏥 Больничные")) {
                        telegramService.sendSilcKeyboard(chatId);
                    } else if (userMessage.equalsIgnoreCase("📄 Создать предложение")) {
                        sendCreateSubmission(chatId);
                    } else if (userMessage.equalsIgnoreCase("📩 Загрузить файл")) {
                        userStateService.setUserState(chatId, "waiting_for_submission_choice");
                        //telegramService.sendMessage(chatId, "Пожалуйста, выберите предложение из списка.");
                        sendSubmissionListWithPagination(chatId, 1);
                    } else if (userMessage.equalsIgnoreCase("📄 Мои предложения")) {
                        sendUserSubmissions(chatId);
                    } else {
                        handleSickLeaveMessage(userMessage, chatId);
                    }
                    break;
            }
        }
    }

    private void handleStartDateInput(long chatId, String userMessage) {
        try {
            Date startDate = parseDate(userMessage);
            userStateService.saveTemporaryData(chatId, "startDate", startDate);
            userStateService.setUserState(chatId, "NEW_SICK_LEAVE_END_DATE");
            telegramService.sendMessage(chatId, "Отлично! Теперь отправьте дату окончания больничного (в формате YYYY-MM-DD).");
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Неверный формат даты. Попробуйте снова. Формат: YYYY-MM-DD.");
        }
    }



    private void handleEndDateInput(long chatId, String userMessage) {
        try {
            Date endDate = parseDate(userMessage);
            Date startDate = (Date) userStateService.getTemporaryData(chatId, "startDate");

            if (endDate.before(startDate)) {
                telegramService.sendMessage(chatId, "Дата окончания не может быть раньше даты начала. Попробуйте снова.");
                return;
            }

            userStateService.saveTemporaryData(chatId, "endDate", endDate);
            userStateService.setUserState(chatId, "NEW_SICK_LEAVE_CONFIRMATION");
            telegramService.sendMessage(chatId, "Ваш больничный с " + startDate + " по " + endDate + " готов к созданию. Подтвердите ввод отправкой 'Да' или отмените, отправив 'Нет'.");
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Неверный формат даты. Попробуйте снова. Формат: YYYY-MM-DD.");
        }
    }

    private void handleSickLeaveConfirmation(long chatId, String userMessage) {
        if (userMessage.equalsIgnoreCase("Да")) {
            Date startDate = (Date) userStateService.getTemporaryData(chatId, "startDate");
            Date endDate = (Date) userStateService.getTemporaryData(chatId, "endDate");

            SickLeave sickLeave = new SickLeave();
            sickLeave.setUserId(getUserIdByChatId(chatId));
            sickLeave.setStartDate(String.valueOf(startDate));
            sickLeave.setEndDate(String.valueOf(endDate));
            sickLeave.setStatus("pending");

            sickLeaveService.saveSickLeave(sickLeave);

            userStateService.clearUserState(chatId);
            telegramService.sendMessage(chatId, "Ваш больничный успешно создан и ожидает подтверждения.");
        } else if (userMessage.equalsIgnoreCase("Нет")) {
            userStateService.clearUserState(chatId);
            telegramService.sendMessage(chatId, "Создание больничного отменено.");
        } else {
            telegramService.sendMessage(chatId, "Пожалуйста, подтвердите создание, отправив 'Да', или отмените, отправив 'Нет'.");
        }
    }

    private void handleCallbackQuery(String callbackData, long chatId) {
        if (callbackData.startsWith("submission_")) {
            handleSubmissionChoice(chatId, callbackData);
        } else if (callbackData.startsWith("page_")) {
            int page = Integer.parseInt(callbackData.split("_")[1]);
            sendSubmissionListWithPagination(chatId, page);
        } else {
        }
    }

    private void sendUserSubmissions(long chatId) {
        List<Submission> userSubmissions = submissionService.getSubmissionsByUser(chatId);

        if (userSubmissions.isEmpty()) {
            telegramService.sendMessage(chatId, "У вас пока нет созданных предложений. Вы можете создать новое предложение, нажав на кнопку '📄 Создать предложение'.");
            return;
        }

        StringBuilder responseText = new StringBuilder("📋 *Ваши предложения:*\n\n");
        for (Submission submission : userSubmissions) {
            responseText.append("📌 *Название*: ").append(submission.getTitle()).append("\n")
                    .append("📝 *Описание*: ").append(submission.getDescription()).append("\n")
                    .append("📅 *Дата создания*: ").append(new SimpleDateFormat("yyyy-MM-dd").format(submission.getSubmittedAt())).append("\n")
                    .append("📂 *Статус*: ").append(getSubmissionStatusEmoji(submission.getStatus())).append(" ").append(submission.getStatus()).append("\n")
                    .append("🔗 *ID*: ").append(submission.getId()).append("\n\n");
        }

        telegramService.sendMessage(chatId, responseText.toString());
    }

    private String getSubmissionStatusEmoji(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "⏳";
            case "approved":
                return "✅";
            case "rejected":
                return "❌";
            default:
                return "🔍";
        }
    }

    private Date parseDate(String dateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateStr);
    }

    private void sendCreateSubmission(long chatId) {
        telegramService.sendMessage(chatId, "Напишите название предложения");
        userStateService.setUserState(chatId, "waiting_for_submission_title");
    }

    private void handleSubmissionTitle(long chatId, String userMessage) {
        String title = userMessage.trim();

        if (title.isEmpty()) {
            telegramService.sendMessage(chatId, "Название не может быть пустым. Пожалуйста, введите название предложения.");
            return;
        }

        userStateService.saveTemporaryData(chatId, "submissionTitle", title);
        telegramService.sendMessage(chatId, "Введите описание вашего предложения.");
        userStateService.setUserState(chatId, "waiting_for_submission_description");
    }

    private void handleSubmissionDescription(long chatId, String userMessage) {
        String description = userMessage.trim();

        if (description.isEmpty()) {
            telegramService.sendMessage(chatId, "Описание не может быть пустым. Пожалуйста, введите описание.");
            return;
        }

        userStateService.saveTemporaryData(chatId, "submissionDescription", description);
        telegramService.sendMessage(chatId, "Ваше предложение будет создано. Подтвердите, что всё верно, отправив 'Да', или отмените, отправив 'Нет'.");
        userStateService.setUserState(chatId, "waiting_for_submission_confirmation");
    }

    private void handleSubmissionConfirmation(long chatId, String userMessage) {
        if (userMessage.equalsIgnoreCase("Да")) {
            String title = (String) userStateService.getTemporaryData(chatId, "submissionTitle");
            String description = (String) userStateService.getTemporaryData(chatId, "submissionDescription");

            Submission newSubmission = new Submission();
            newSubmission.setTitle(title);
            newSubmission.setDescription(description);
            newSubmission.setSubmittedBy(getUserIdByChatId(chatId));
            newSubmission.setSubmittedAt(new Date());
            newSubmission.setStatus("pending");

            submissionService.saveSubmission(newSubmission);

            userStateService.clearUserState(chatId);
            telegramService.sendMessage(chatId, "Ваше предложение успешно создано и отправлено на рассмотрение.");
        } else if (userMessage.equalsIgnoreCase("Нет")) {
            telegramService.sendMessage(chatId, "Создание предложения отменено.");
            userStateService.clearUserState(chatId);
        } else {
            telegramService.sendMessage(chatId, "Пожалуйста, подтвердите создание, отправив 'Да', или отмените, отправив 'Нет'.");
        }
    }

    private void handleSubmissionChoice(long chatId, String callbackData) {
        String submissionId = callbackData.replace("submission_", "");
        Submission submission = submissionService.findById(submissionId);

        if (submission == null) {
            telegramService.sendMessage(chatId, "Ошибка: предложение не найдено.");
            return;
        }

        userStateService.saveTemporaryData(chatId, "selectedSubmission", submission);
        userStateService.setUserState(chatId, "waiting_for_file_upload");

        Document lastDocument = (Document) userStateService.getTemporaryData(chatId, "lastDocument");
        if (lastDocument != null) {
            telegramService.sendMessage(chatId, "Вы выбрали предложение: " + submission.getTitle() + ". Использовать последний загруженный файл '" + lastDocument.getFileName() + "'? Отправьте 'Да' для использования или загрузите новый файл.");
        } else {
            telegramService.sendMessage(chatId, "Вы выбрали предложение: " + submission.getTitle() + ". Теперь отправьте файл для загрузки.");
        }
    }
    private void sendSubmissionListWithPagination(long chatId, int page) {
        List<Submission> submissions = submissionService.getSubmissionsByUser(chatId);
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) submissions.size() / pageSize);

        if (submissions.isEmpty()) {
            telegramService.sendMessage(chatId, "У вас нет предложений для прикрепления файлов.");
            userStateService.clearUserState(chatId);
            return;
        }

        List<Submission> paginatedSubmissions = submissions.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Submission submission : paginatedSubmissions) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(submission.getTitle())
                    .callbackData("submission_" + submission.getId())
                    .build();
            keyboard.add(List.of(button));
        }

        if (totalPages > 1) {
            List<InlineKeyboardButton> navigationButtons = new ArrayList<>();
            if (page > 1) {
                navigationButtons.add(InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("page_" + (page - 1))
                        .build());
            }
            if (page < totalPages) {
                navigationButtons.add(InlineKeyboardButton.builder()
                        .text("Вперёд ➡️")
                        .callbackData("page_" + (page + 1))
                        .build());
            }
            keyboard.add(navigationButtons);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите предложение, к которому нужно прикрепить файл:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        telegramService.sendInlineKeyboard(chatId, message);
    }

    public void handleFileUploadProcessing(long chatId, Update update) {
        Document userDocument = null;

        // Проверяем, есть ли документ в текущем сообщении
        if (update.hasMessage() && update.getMessage().hasDocument()) {
            userDocument = update.getMessage().getDocument();
        } else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equalsIgnoreCase("Да")) {
            // Если пользователь ответил "Да", используем последний сохранённый документ
            userDocument = (Document) userStateService.getTemporaryData(chatId, "lastDocument");
        }

        if (userDocument == null) {
            telegramService.sendMessage(chatId, "Ошибка: отправьте документ для загрузки или подтвердите использование последнего файла, отправив 'Да'.");
            return;
        }

        String fileId = userDocument.getFileId();

        try {
            org.telegram.telegrambots.meta.api.objects.File telegramFile = telegramService.getFile(fileId);

            String originalFileName = userDocument.getFileName();
            String localFileName = "uploads/" + chatId + "_" + System.currentTimeMillis() + "_" + originalFileName;

            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Создаём директорию, если её нет
            }

            telegramService.downloadFile(telegramFile.getFilePath(), new File(localFileName));

            Submission selectedSubmission = (Submission) userStateService.getTemporaryData(chatId, "selectedSubmission");

            if (selectedSubmission == null) {
                telegramService.sendMessage(chatId, "Ошибка: не выбрано предложение для загрузки файла.");
                return;
            }

            selectedSubmission.setFileUrl(localFileName);
            submissionService.saveSubmission(selectedSubmission);

            telegramService.sendMessage(chatId, "Файл успешно загружен и прикреплён к предложению: " + selectedSubmission.getTitle());
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Ошибка при загрузке файла: " + e.getMessage());
        } finally {
            userStateService.clearUserState(chatId);
        }
    }
    private String getUserIdByChatId(long chatId) {
        return String.valueOf(chatId); // Пример реализации
    }

    private void handleSickLeaveMessage(String userMessage, long chatId) {
        if (userMessage.equals("📅 История больничных")) {
            sendSickLeaveHistory(chatId);
        } else if (userMessage.equals("📝 Открыть новый больничный")) {
            sendNewSickLeaveForm(chatId);
        }
    }

    private void sendSickLeaveHistory(long chatId) {
        List<SickLeave> sickLeaves = sickLeaveService.getSickLeavesByUserId(chatId);

        if (sickLeaves.isEmpty()) {
            telegramService.sendMessage(chatId, "🚫 У вас нет истории больничных.");
            return;
        }

        StringBuilder responseText = new StringBuilder("📜 *Ваша история больничных:*\n\n");
        for (SickLeave sickLeave : sickLeaves) {
            responseText.append("📅 *Дата начала*: ").append(sickLeave.getStartDate()).append("\n")
                    .append("🏥 *Дата окончания*: ").append(sickLeave.getEndDate()).append("\n")
                    .append("🔖 *Статус*: ").append(sickLeave.getStatus()).append("\n\n");
        }

        telegramService.sendMessage(chatId, responseText.toString());
    }

    private void sendNewSickLeaveForm(long chatId) {
        telegramService.sendMessage(chatId, "Пожалуйста, отправьте дату начала больничного (например, 2025-01-25).");
        userStateService.setUserState(chatId, "NEW_SICK_LEAVE_START_DATE");
    }
}