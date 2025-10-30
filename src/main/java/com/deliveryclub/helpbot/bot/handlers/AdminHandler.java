package com.deliveryclub.helpbot.bot.handlers;

import com.deliveryclub.helpbot.bot.keyboards.KeyboardService;
import com.deliveryclub.helpbot.models.Question;
import com.deliveryclub.helpbot.models.Submission;
import com.deliveryclub.helpbot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AdminHandler {

    @Autowired
    UserStateService userStateService;
    @Autowired
    TelegramService telegramService;
    @Autowired
    QuestionService questionService;
    @Autowired
    SubmissionService submissionService;
    @Autowired
    UserService userService;
    @Autowired
    KeyboardService keyboardService;

    public void handlerAdminMessage(Update update, String userMessage, long chatId) {
        String state = userStateService.getUserState(chatId);

        // Обработка состояний
        switch (state) {
            case "waiting_for_question_category":
                handleCreateQuestionCategory(chatId, userMessage);
                break;
            case "waiting_for_question_text":
                handleCreateQuestionText(chatId, userMessage);
                break;
            case "waiting_for_answer_text":
                handleCreateAnswerText(chatId, userMessage);
                break;
            case "waiting_for_search_keyword":
                handleSearchQuestionByKeyword(chatId, userMessage);
                break;
            case "waiting_for_question_id_for_update":
                handleUpdateAnswerForQuestion(chatId, userMessage);
                break;
            case "waiting_for_question_id_for_deletion":
                handleDeleteQuestionById(chatId, userMessage);
                break;
            case "waiting_for_new_answer":
                handleUpdateAnswerText(chatId, userMessage);
                break;
            case "waiting_for_submission_approval":
                handleSubmissionApproval(chatId, userMessage);
                break;
            case "waiting_for_submission_rejection":
                handleSubmissionRejection(chatId, userMessage);
                break;
            case "waiting_for_username":
                handleUserMessage(update, userMessage, chatId);
                break;
            case "waiting_for_telegram_id":
                handleUserMessage(update, userMessage, chatId);
                break;
            case "waiting_for_role":
                handleUserMessage(update, userMessage, chatId);
                break;
            case "waiting_for_department":
                handleUserMessage(update, userMessage, chatId);
                break;
            case "waiting_for_password":
                handleUserMessage(update, userMessage, chatId);
                break;
            default:
                // Обработка команд администратора
                if (userMessage.equals("📊 Добавить нового пользователя")) {
                    telegramService.sendMessage(chatId, "✨ Пожалуйста, отправьте имя пользователя ✨");
                    userStateService.setUserState(chatId, "waiting_for_username");
                } else if (userMessage.equals("📝 Управление вопросами")) {
                    telegramService.sendQuestionKeyboard(chatId);
                } else if (userMessage.equals("🆕 Создать новый вопрос")) {
                    telegramService.sendMessage(chatId, "🖋️ Пожалуйста, введите категорию вопроса.");
                    userStateService.setUserState(chatId, "waiting_for_question_category");
                } else if (userMessage.equals("🔍 Поиск вопроса")) {
                    telegramService.sendMessage(chatId, "🔑 Введите ключевое слово для поиска вопроса.");
                    userStateService.setUserState(chatId, "waiting_for_search_keyword");
                }
                else if (userMessage.equals("✏️ Обновить ответ на вопрос")) {
                    List<Question> questions = questionService.getAllQuestions();
                    if (questions.isEmpty()) {
                        telegramService.sendMessage(chatId, "Список вопросов пуст.");
                    } else {
                        sendQuestionListForUpdate(chatId, questions, 1);
                    }

                } else if (userMessage.equals("❌ Удалить вопрос")) {
                    System.out.println("demi");
                    List<Question> questions = questionService.getAllQuestions();
                    for (Question question : questions) {
                        System.out.println(question.getQuestion());
                    }
                    if (questions.isEmpty()) {
                        telegramService.sendMessage(chatId, "Список вопросов пуст.");
                    } else {
                        sendQuestionListForDeletion(chatId, questions, 1);
                    }
                } else if (userMessage.equals("✅ Одобрить предложения")) {
                    handleApproveSubmission(chatId);
                } else if (userMessage.equals("❌ Отклонить предложения")) {
                    handleRejectSubmission(chatId);
                } else if (userMessage.equals("📊 Статистика")) {
                    handleStatistics(chatId);
                }
                break;
        }

        // Обработка callback-запросов
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();

            System.out.println("Callback data received: " + callbackData); // Добавьте для отладки

            if (callbackData.equals("create_question")) {
                telegramService.sendMessage(callbackChatId, "🖋️ Пожалуйста, введите категорию вопроса.");
                userStateService.setUserState(callbackChatId, "waiting_for_question_category");
            } else if (callbackData.equals("search_question")) {
                telegramService.sendMessage(callbackChatId, "🔑 Введите ключевое слово для поиска вопроса.");
                userStateService.setUserState(callbackChatId, "waiting_for_search_keyword");
            } else if (callbackData.equals("update_answer")) {
                telegramService.sendMessage(callbackChatId, "🔄 Введите ID вопроса для обновления ответа.");
                userStateService.setUserState(callbackChatId, "waiting_for_question_id_for_update");
            } else if (callbackData.equals("delete_question")) {
                telegramService.sendMessage(callbackChatId, "🚮 Введите ID вопроса для удаления.");
                userStateService.setUserState(callbackChatId, "waiting_for_question_id_for_deletion");
            } else if (callbackData.startsWith("list_questions_")) {
                int page = Integer.parseInt(callbackData.replace("list_questions_", ""));
                List<Question> questions = questionService.getAllQuestions();
                if (questions.isEmpty()) {
                    telegramService.sendMessage(callbackChatId, "Список вопросов пуст.");
                } else {
                    telegramService.sendQuestionsList(callbackChatId, questions, page);
                }
            } else if (callbackData.startsWith("questions_page_")) {
                int page = Integer.parseInt(callbackData.replace("questions_page_", ""));
                List<Question> questions = questionService.getAllQuestions();
                telegramService.sendQuestionsList(callbackChatId, questions, page);
            } else if (callbackData.startsWith("select_question_")) {
                String questionId = callbackData.replace("select_question_", "");
                Question question = questionService.getQuestionById(questionId);
                if (question != null) {
                    String response = String.format(
                            "Вопрос: %s\nКатегория: %s\nОтвет: %s\nID: %s",
                            question.getQuestion(), question.getCategory(), question.getAnswer(), question.getId()
                    );
                    telegramService.sendMessage(callbackChatId, response);
                } else {
                    telegramService.sendMessage(callbackChatId, "Вопрос с ID " + questionId + " не найден.");
                }
            } else if (callbackData.startsWith("update_question_")) {
                String questionId = callbackData.replace("update_question_", "");
                Question question = questionService.getQuestionById(questionId);
                if (question != null) {
                    telegramService.sendMessage(callbackChatId, "Введите новый ответ для вопроса: " + question.getQuestion());
                    userStateService.setUserState(callbackChatId, "waiting_for_new_answer");
                    userStateService.saveTemporaryData(callbackChatId, "questionToUpdate", question);
                } else {
                    telegramService.sendMessage(callbackChatId, "Вопрос с ID " + questionId + " не найден.");
                }
            } else if (callbackData.startsWith("delete_question_")) {
                String questionId = callbackData.replace("delete_question_", "");
                Question question = questionService.getQuestionById(questionId);
                if (question != null) {
                    questionService.deleteQuestion(question);
                    telegramService.sendMessage(callbackChatId, "Вопрос с ID " + questionId + " успешно удален.");
                } else {
                    telegramService.sendMessage(callbackChatId, "Вопрос с ID " + questionId + " не найден.");
                }
            } else if (callbackData.startsWith("update_page_")) {
                int page = Integer.parseInt(callbackData.replace("update_page_", ""));
                List<Question> questions = questionService.getAllQuestions();
                sendQuestionListForUpdate(callbackChatId, questions, page);
            } else if (callbackData.startsWith("delete_page_")) {
                int page = Integer.parseInt(callbackData.replace("delete_page_", ""));
                List<Question> questions = questionService.getAllQuestions();
                sendQuestionListForDeletion(callbackChatId, questions, page);
            }
        }
    }

    // Обработка создания вопроса: категория
    private void handleCreateQuestionCategory(long chatId, String userMessage) {
        userStateService.saveTemporaryData(chatId, "category", userMessage);
        telegramService.sendMessage(chatId, "Введите сам вопрос.");
        userStateService.setUserState(chatId, "waiting_for_question_text");
    }

    private void sendQuestionListForUpdate(long chatId, List<Question> questions, int page) {
        int pageSize = 5; // Количество вопросов на странице
        int totalPages = (int) Math.ceil((double) questions.size() / pageSize);

        List<Question> paginatedQuestions = questions.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Question question : paginatedQuestions) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(question.getQuestion() + " (ID: " + question.getId() + ")")
                    .callbackData("update_question_" + question.getId())
                    .build();
            keyboard.add(List.of(button));
        }

        if (totalPages > 1) {
            List<InlineKeyboardButton> navigation = new ArrayList<>();
            if (page > 1) {
                navigation.add(InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("update_page_" + (page - 1))
                        .build());
            }
            if (page < totalPages) {
                navigation.add(InlineKeyboardButton.builder()
                        .text("Вперёд ➡️")
                        .callbackData("update_page_" + (page + 1))
                        .build());
            }
            keyboard.add(navigation);
        }

        markup.setKeyboard(keyboard);
        telegramService.sendInlineKeyboard(chatId, "Выберите вопрос для обновления:", markup);
    }

    private void sendQuestionListForDeletion(long chatId, List<Question> questions, int page) {
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) questions.size() / pageSize);

        List<Question> paginatedQuestions = questions.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Question question : paginatedQuestions) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(question.getQuestion() + " (ID: " + question.getId() + ")")
                    .callbackData("delete_question_" + question.getId())
                    .build();
            keyboard.add(List.of(button));
        }

        if (totalPages > 1) {
            List<InlineKeyboardButton> navigation = new ArrayList<>();
            if (page > 1) {
                navigation.add(InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("delete_page_" + (page - 1))
                        .build());
            }
            if (page < totalPages) {
                navigation.add(InlineKeyboardButton.builder()
                        .text("Вперёд ➡️")
                        .callbackData("delete_page_" + (page + 1))
                        .build());
            }
            keyboard.add(navigation);
        }

        markup.setKeyboard(keyboard);
        telegramService.sendInlineKeyboard(chatId, "Выберите вопрос для удаления:", markup);
    }

    // Обработка создания вопроса: текст вопроса
    private void handleCreateQuestionText(long chatId, String userMessage) {
        userStateService.saveTemporaryData(chatId, "question", userMessage);
        telegramService.sendMessage(chatId, "Введите ответ на вопрос.");
        userStateService.setUserState(chatId, "waiting_for_answer_text");
    }

    // Обработка создания вопроса: текст ответа
    private void handleCreateAnswerText(long chatId, String userMessage) {
        String category = (String) userStateService.getTemporaryData(chatId, "category");
        String questionText = (String) userStateService.getTemporaryData(chatId, "question");

        Question question = new Question();
        question.setCategory(category);
        question.setQuestion(questionText);
        question.setAnswer(userMessage);
        question.setLastUpdated(new Date());
        question.setAddedBy(getUserIdByChatId(chatId));

        questionService.createQuestion(question);

        telegramService.sendMessage(chatId, "Вопрос успешно создан.");
        userStateService.clearUserState(chatId);
    }

    // Получение userId по chatId
    private String getUserIdByChatId(long chatId) {
        return String.valueOf(chatId); // Предполагается, что chatId используется как userId
    }

    // Обработка поиска вопроса по ключевому слову
    private void handleSearchQuestionByKeyword(long chatId, String userMessage) {
        List<Question> questions = questionService.searchQuestionsByKeyword(userMessage);

        if (questions.isEmpty()) {
            telegramService.sendMessage(chatId, "Вопросы с таким ключевым словом не найдены.");
            userStateService.clearUserState(chatId);
            return;
        }

        StringBuilder sb = new StringBuilder("Найденные вопросы:\n");
        for (Question question : questions) {
            sb.append(question.getQuestion()).append(" (ID: ").append(question.getId()).append(")\n");
        }

        telegramService.sendMessage(chatId, sb.toString());
        userStateService.clearUserState(chatId);
    }

    // Обработка обновления ответа на вопрос: ввод нового ответа
    private void handleUpdateAnswerText(long chatId, String userMessage) {
        Question questionToUpdate = (Question) userStateService.getTemporaryData(chatId, "questionToUpdate");

        if (questionToUpdate == null) {
            telegramService.sendMessage(chatId, "Не найден вопрос для обновления.");
            userStateService.clearUserState(chatId);
            return;
        }

        questionToUpdate.setAnswer(userMessage);
        questionToUpdate.setLastUpdated(new Date());

        questionService.updateQuestion(questionToUpdate);

        telegramService.sendMessage(chatId, "Ответ успешно обновлен.");
        userStateService.clearUserState(chatId);
    }

    // Обработка обновления ответа: ввод ID вопроса
    private void handleUpdateAnswerForQuestion(long chatId, String userMessage) {
        try {
            String questionId = userMessage.trim();
            Question question = questionService.getQuestionById(questionId);

            if (question == null) {
                telegramService.sendMessage(chatId, "Вопрос с таким ID не найден.");
                userStateService.clearUserState(chatId);
                return;
            }

            telegramService.sendMessage(chatId, "Введите новый ответ на вопрос.");
            userStateService.setUserState(chatId, "waiting_for_new_answer");
            userStateService.saveTemporaryData(chatId, "questionToUpdate", question);
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Произошла ошибка при обновлении ответа.");
        }
    }

    // Обработка удаления вопроса
    private void handleDeleteQuestionById(long chatId, String userMessage) {
        try {
            String questionId = userMessage.trim();
            Question question = questionService.getQuestionById(questionId);

            if (question == null) {
                telegramService.sendMessage(chatId, "Вопрос с таким ID не найден.");
                userStateService.clearUserState(chatId);
                return;
            }

            questionService.deleteQuestion(question);

            telegramService.sendMessage(chatId, "Вопрос успешно удален.");
            userStateService.clearUserState(chatId);
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Произошла ошибка при удалении вопроса.");
        }
    }

    // Обработка одобрения предложения
    private void handleSubmissionApproval(long chatId, String userMessage) {
        try {
            String submissionId = userMessage.trim();
            Submission submission = submissionService.getSubmissionById(submissionId);

            if (submission == null) {
                telegramService.sendMessage(chatId, "Предложение с таким ID не найдено.");
                return;
            }

            submission.setStatus("approved");
            submission.setReviewedBy(getUserIdByChatId(chatId));
            submission.setReviewedAt(new Date());
            submissionService.saveSubmission(submission);

            telegramService.sendMessage(chatId, "Предложение успешно одобрено.");
            userStateService.clearUserState(chatId);
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Произошла ошибка при обработке предложения.");
        }
    }

    // Обработка отклонения предложения
    private void handleSubmissionRejection(long chatId, String userMessage) {
        try {
            String submissionId = userMessage.trim();
            Submission submission = submissionService.getSubmissionById(submissionId);

            if (submission == null) {
                telegramService.sendMessage(chatId, "Предложение с таким ID не найдено.");
                return;
            }

            submission.setStatus("rejected");
            submission.setReviewedBy(getUserIdByChatId(chatId));
            submission.setReviewedAt(new Date());
            submissionService.saveSubmission(submission);

            telegramService.sendMessage(chatId, "Предложение успешно отклонено.");
            userStateService.clearUserState(chatId);
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Произошла ошибка при обработке предложения.");
        }
    }

    // Обработка одобрения предложений
    private void handleApproveSubmission(long chatId) {
        List<Submission> pendingSubmissions = submissionService.getPendingSubmissions();
        if (pendingSubmissions.isEmpty()) {
            telegramService.sendMessage(chatId, "Нет предложений на одобрение.");
            return;
        }

        StringBuilder sb = new StringBuilder("Предложения на одобрение:\n");
        for (Submission submission : pendingSubmissions) {
            sb.append(submission.getTitle()).append(" - ").append(submission.getSubmittedBy()).append(" (ID: ").append(submission.getId()).append(")\n");
        }

        telegramService.sendMessage(chatId, sb.toString() + "Отправьте ID предложения для одобрения.");
        userStateService.setUserState(chatId, "waiting_for_submission_approval");
    }

    // Обработка отклонения предложений
    private void handleRejectSubmission(long chatId) {
        List<Submission> pendingSubmissions = submissionService.getPendingSubmissions();
        if (pendingSubmissions.isEmpty()) {
            telegramService.sendMessage(chatId, "Нет предложений на отклонение.");
            return;
        }

        StringBuilder sb = new StringBuilder("Предложения на отклонение:\n");
        for (Submission submission : pendingSubmissions) {
            sb.append(submission.getTitle()).append(" - ").append(submission.getSubmittedBy()).append(" (ID: ").append(submission.getId()).append(")\n");
        }

        telegramService.sendMessage(chatId, sb.toString() + "Отправьте ID предложения для отклонения.");
        userStateService.setUserState(chatId, "waiting_for_submission_rejection");
    }

    // Обработка статистики
    private void handleStatistics(long chatId) {
        long approvedCount = submissionService.countSubmissionsByStatus("approved");
        long rejectedCount = submissionService.countSubmissionsByStatus("rejected");
        long pendingCount = submissionService.countSubmissionsByStatus("pending");

        String stats = String.format("Статистика:\nОдобрено: %d\nОтклонено: %d\nОжидает: %d",
                approvedCount, rejectedCount, pendingCount);
        telegramService.sendMessage(chatId, stats);
    }

    // Обработка создания нового пользователя
    private void handleUserMessage(Update update, String userMessage, long chatId) {
        String state = userStateService.getUserState(chatId);

        if ("waiting_for_username".equals(state)) {
            userStateService.saveTemporaryData(chatId, "username", userMessage);
            telegramService.sendMessage(chatId, "Теперь отправьте Telegram ID пользователя.");
            userStateService.setUserState(chatId, "waiting_for_telegram_id");
        } else if ("waiting_for_telegram_id".equals(state)) {
            try {
                Long telegramId = Long.parseLong(userMessage);
                userStateService.saveTemporaryData(chatId, "telegramId", telegramId);
                telegramService.sendMessage(chatId, "Теперь отправьте роль пользователя (например, admin, user, moderator).");
                userStateService.setUserState(chatId, "waiting_for_role");
            } catch (NumberFormatException e) {
                telegramService.sendMessage(chatId, "Неверный формат Telegram ID. Пожалуйста, отправьте корректный Telegram ID.");
            }
        } else if ("waiting_for_role".equals(state)) {
            userStateService.saveTemporaryData(chatId, "role", userMessage);
            telegramService.sendMessage(chatId, "Теперь отправьте департамент пользователя.");
            userStateService.setUserState(chatId, "waiting_for_department");
        } else if ("waiting_for_department".equals(state)) {
            userStateService.saveTemporaryData(chatId, "department", userMessage);
            telegramService.sendMessage(chatId, "Теперь отправьте пароль пользователя.");
            userStateService.setUserState(chatId, "waiting_for_password");
        } else if ("waiting_for_password".equals(state)) {
            String username = (String) userStateService.getTemporaryData(chatId, "username");
            Long telegramId = (Long) userStateService.getTemporaryData(chatId, "telegramId");
            String role = (String) userStateService.getTemporaryData(chatId, "role");
            String department = (String) userStateService.getTemporaryData(chatId, "department");
            String password = userMessage;

            userService.createNewUser(username, telegramId, role, department, password);

            userStateService.clearUserState(chatId);
            telegramService.sendMessage(chatId, "Пользователь успешно добавлен!");
        }
    }
}