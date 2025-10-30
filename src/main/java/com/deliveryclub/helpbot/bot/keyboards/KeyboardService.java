package com.deliveryclub.helpbot.bot.keyboards;

import com.deliveryclub.helpbot.models.Question;
import com.deliveryclub.helpbot.models.User;
import com.deliveryclub.helpbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardService {

    private static final int QUESTIONS_PER_PAGE = 5;

    @Autowired
    private UserService userService;
    public ReplyKeyboardMarkup getUserKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("💼 Практики"));
        row1.add(new KeyboardButton("🏥 Больничные"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("📄 Мои предложения"));
        row2.add(new KeyboardButton("📩 Загрузить файл"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📄 Создать предложение"));
        row3.add(new KeyboardButton("⏳ Взять отгул / Форс-мажор"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("💸 Запрос компенсации"));
        row4.add(new KeyboardButton("📅 Мои смены"));

        // Новая строка с кнопками
        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton("📜 Все правила"));
        row5.add(new KeyboardButton("❓ Команды"));

        keyboardMarkup.setKeyboard(List.of(row1, row2, row3, row4, row5));
        return keyboardMarkup;
    }

    // Инлайн-клавиатура для выбора типа компенсации
    public InlineKeyboardMarkup getCompensationTypeKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Переработка"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton overtimeButton = new InlineKeyboardButton();
        overtimeButton.setText("⏰ Переработка");
        overtimeButton.setCallbackData("compensation_overtime");
        row1.add(overtimeButton);
        rows.add(row1);

        // Кнопка "Транспорт"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton transportButton = new InlineKeyboardButton();
        transportButton.setText("🚗 Транспорт");
        transportButton.setCallbackData("compensation_transport");
        row2.add(transportButton);
        rows.add(row2);

        // Кнопка "Другое"
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton otherButton = new InlineKeyboardButton();
        otherButton.setText("❓ Другое");
        otherButton.setCallbackData("compensation_other");
        row3.add(otherButton);
        rows.add(row3);

        markup.setKeyboard(rows);
        return markup;
    }

    // Инлайн-клавиатура для выбора периода расписания
    public InlineKeyboardMarkup getShiftPeriodKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Сегодня"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton todayButton = new InlineKeyboardButton();
        todayButton.setText("📅 Сегодня");
        todayButton.setCallbackData("shift_today");
        row1.add(todayButton);
        rows.add(row1);

        // Кнопка "Эта неделя"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton weekButton = new InlineKeyboardButton();
        weekButton.setText("📆 Эта неделя");
        weekButton.setCallbackData("shift_week");
        row2.add(weekButton);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getRequestTypeKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Взять отгул"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton dayOffButton = new InlineKeyboardButton();
        dayOffButton.setText("⏳ Взять отгул");
        dayOffButton.setCallbackData("request_day_off");
        row1.add(dayOffButton);
        rows.add(row1);

        // Кнопка "Форс-мажор"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton forceMajeureButton = new InlineKeyboardButton();
        forceMajeureButton.setText("🚨 Форс-мажор");
        forceMajeureButton.setCallbackData("request_force_majeure");
        row2.add(forceMajeureButton);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    // Клавиатура для администратора (admin)
    public ReplyKeyboardMarkup getAdminKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📝 Управление вопросами"));
        row1.add(new KeyboardButton("✅ Одобрить предложения"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("📊 Статистика"));
        row2.add(new KeyboardButton("❌ Отклонить предложения"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📊 Добавить нового пользователя"));

        keyboardMarkup.setKeyboard(List.of(row1, row2, row3));
        return keyboardMarkup;
    }

/*    public InlineKeyboardMarkup getRequestModerationKeyboard(String requestId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(new InlineKeyboardButton().setText("✔️ Утвердить").setCallbackData("approve_request_" + requestId)));
        rows.add(List.of(new InlineKeyboardButton().setText("❌ Отклонить").setCallbackData("reject_request_" + requestId)));
        markup.setKeyboard(rows);
        return markup;
    }*/

    // Клавиатура для модератора (moderator)
    public ReplyKeyboardMarkup getModeratorKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🔍 Проверить предложения"));
        row1.add(new KeyboardButton("🏥 Проверить больничные"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("⏳ Проверить отгулы/форс-мажоры"));
        row2.add(new KeyboardButton("💸 Проверить компенсации"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("✔️ Утвердить файл"));
        row3.add(new KeyboardButton("❌ Отклонить файл"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("📄 Просмотр всех предложений"));
        row4.add(new KeyboardButton("📅 Добавить смену")); // Новая кнопка
        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton("📋 Назначить задачу")); // Новая кнопка

        keyboardMarkup.setKeyboard(List.of(row1, row2, row3, row4, row5));
        return keyboardMarkup;
    }

    // Инлайн-клавиатура для выбора пользователя
    public InlineKeyboardMarkup getUserSelectionKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<User> users = userService.getAllUsers(); // Получаем всех пользователей
        for (User user : users) {
            InlineKeyboardButton userButton = InlineKeyboardButton.builder()
                    .text(user.getUsername() + " (" + user.getTelegramId() + ")")
                    .callbackData("select_user_" + user.getTelegramId())
                    .build();
            rows.add(List.of(userButton));
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getFileModerationKeyboard(String fileId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton approveButton = InlineKeyboardButton.builder()
                .text("✔️ Утвердить")
                .callbackData("approve_file_" + fileId)
                .build();
        rows.add(List.of(approveButton));

        InlineKeyboardButton rejectButton = InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject_file_" + fileId)
                .build();
        rows.add(List.of(rejectButton));

        markup.setKeyboard(rows);
        return markup;
    }



    public InlineKeyboardMarkup getSubmissionModerationKeyboard(String submissionId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton approveButton = InlineKeyboardButton.builder()
                .text("✔️ Утвердить")
                .callbackData("approve_submission_" + submissionId)
                .build();
        rows.add(List.of(approveButton));

        InlineKeyboardButton rejectButton = InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject_submission_" + submissionId)
                .build();
        rows.add(List.of(rejectButton));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getCompensationModerationKeyboard(String requestId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton approveButton = InlineKeyboardButton.builder()
                .text("✔️ Утвердить")
                .callbackData("approve_compensation_" + requestId)
                .build();
        rows.add(List.of(approveButton));

        InlineKeyboardButton rejectButton = InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject_compensation_" + requestId)
                .build();
        rows.add(List.of(rejectButton));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getUserRequestModerationKeyboard(String requestId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton approveButton = InlineKeyboardButton.builder()
                .text("✔️ Утвердить")
                .callbackData("approve_request_" + requestId)
                .build();
        rows.add(List.of(approveButton));

        InlineKeyboardButton rejectButton = InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject_request_" + requestId)
                .build();
        rows.add(List.of(rejectButton));

        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup getSickLeaveKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📅 История больничных"));
        row1.add(new KeyboardButton("📝 Открыть новый больничный"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);

        return markup;
    }

    public InlineKeyboardMarkup getSickLeaveModerationKeyboard(String sickLeaveId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Утвердить"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton approveButton = new InlineKeyboardButton();
        approveButton.setText("✔️ Утвердить");
        approveButton.setCallbackData("approve_sickleave_" + sickLeaveId);
        row1.add(approveButton);
        rows.add(row1);

        // Кнопка "Отклонить"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton rejectButton = new InlineKeyboardButton();
        rejectButton.setText("❌ Отклонить");
        rejectButton.setCallbackData("reject_sickleave_" + sickLeaveId);
        row2.add(rejectButton);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup getQuestionManagementKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🆕 Создать новый вопрос"));
        row1.add(new KeyboardButton("🔍 Поиск вопроса"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("✏️ Обновить ответ на вопрос"));
        row2.add(new KeyboardButton("❌ Удалить вопрос"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }

    public InlineKeyboardMarkup getQuestionsListKeyboard(List<Question> questions, int page) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Вычисление границ текущей страницы
        int totalPages = (int) Math.ceil((double) questions.size() / QUESTIONS_PER_PAGE);
        int start = page * QUESTIONS_PER_PAGE;
        int end = Math.min(start + QUESTIONS_PER_PAGE, questions.size());

        // Добавление кнопок для вопросов
        for (int i = start; i < end; i++) {
            Question question = questions.get(i);
            InlineKeyboardButton questionButton = InlineKeyboardButton.builder()
                    .text(question.getQuestion() + " (ID: " + question.getId() + ")")
                    .callbackData("select_question_" + question.getId())
                    .build();
            rows.add(List.of(questionButton));
        }

        // Кнопки пагинации
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (page > 0) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("⬅️ Назад")
                    .callbackData("questions_page_" + (page - 1))
                    .build());
        }
        if (page < totalPages - 1) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Вперёд ➡️")
                    .callbackData("questions_page_" + (page + 1))
                    .build());
        }
        if (!navigationRow.isEmpty()) {
            rows.add(navigationRow);
        }

        markup.setKeyboard(rows);
        return markup;
    }




}