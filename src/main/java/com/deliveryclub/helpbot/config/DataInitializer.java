package com.deliveryclub.helpbot.config;

import com.deliveryclub.helpbot.models.*;
import com.deliveryclub.helpbot.service.CallRuleService;
import com.deliveryclub.helpbot.service.DictionaryService;
import com.deliveryclub.helpbot.service.LinkService;
import com.deliveryclub.helpbot.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private CallRuleService callRuleService;

    @Bean
    public CommandLineRunner initCallRules() {
        return args -> {
            // Общие правила
            CallRuleCategory general = new CallRuleCategory("Общие правила", Arrays.asList(
                    new CallRule("Делайте по 2 дозвона на номер курьера", null),
                    new CallRule("Делайте по 1 дозвону на каждый номер из карточки ресторана/магазина без перерыва между ними, длительность каждого 35-40 секунд (после звонок сбрасывается автоматически).", null),
                    new CallRule("Если у партнёра в карточке всего 1 номер или вы видите одинаковый номер во всех строках, на него нужно позвонить два раза (Кроме Х5, «Магнит» и «Азбука Вкуса»)", null),
                    new CallRule("В первую очередь совершите звонки на общие номера. Последним звоните ЛПР", null),
                    new CallRule("Если заказ активен, звоним ЛПР в любое время", null),
                    new CallRule("Если заказ доставлен, звоним ЛПР с 10:00 до 22:00 по времени города", null),
                    new CallRule("Не звоним ЛПР Пятёрочка/Перекрёсток", null)
            ));
            callRuleService.addCategory(general);

            // Исключения
            CallRuleCategory exceptions = new CallRuleCategory("Исключения", Arrays.asList(
                    new CallRule("«Азбука Вкуса» — сразу звоним на номер ЛПР", null),
                    new CallRule("«Магнит» — +78007075724, после реакции голосового помощника, (доб. 3)", null),
                    new CallRule("«2 Берега» — +78122445057", null),
                    new CallRule("«Fix Price» — НЕ звоним", null),
                    new CallRule("«Лента» — НЕ звоним", null)
            ));
            callRuleService.addCategory(exceptions);

            // Короткие номера Х5
            CallRuleCategory x5Numbers = new CallRuleCategory("Короткие номера Х5", Arrays.asList(
                    new CallRule("Пятёрочка/Виктория: +74950000010", null),
                    new CallRule("Перекрёсток, Перекрёсток Кафе, Зеленая Линия: +74950000011", null),
                    new CallRule("Чижик: +74950000012", null),
                    new CallRule("Слата/Красный Яр/Батон: +74950000019", null)
            ));
            callRuleService.addCategory(x5Numbers);

            // Подробности и ссылки
            CallRuleCategory details = new CallRuleCategory("Подробности и ссылки", Arrays.asList(
                    new CallRule("Логика — Как правильно звонить партнеру", "https://bilim.yandex-team.ru/knowledge/181096?selectedId=knowledge-58553-181096#obshie-pravila-dozvona-do-klienta/partnera/kurera"),
                    new CallRule("Инструкция — Как звонить на короткие номера", "https://bilim.yandex-team.ru/knowledge/181160?selectedId=knowledge-58555-181160#kak-dejstvovat?"),
                    new CallRule("Не звоним «Fix Price» и «Лента»", "https://bilim.yandex-team.ru/knowledge/181249?selectedId=knowledge-62420-181249"),
                    new CallRule("2 берега", "https://t.me/c/1236541570/12632")
            ));
            callRuleService.addCategory(details);
        };
    }

    @Bean
    public CommandLineRunner initShifts() {
        return args -> {
            // Пример смен для пользователя с chatId 12345
            shiftService.saveShift(new Shift("757122930", "2025-03-17", "10:00", "18:00"));
            shiftService.saveShift(new Shift("757122930", "2025-03-18", "12:00", "20:00"));
            shiftService.saveShift(new Shift("757122930", "2025-03-19", "09:00", "17:00"));
        };
    }

    @Bean
    public CommandLineRunner initLinks() {
        return args -> {
            // Рабочие инструменты
            LinkCategory tools = new LinkCategory("Рабочие инструменты", Arrays.asList(
                    new Link("БИЛИМ, ЛОГИКА", "https://bilim.yandex-team.ru/service/2160", "Логика и аналитика"),
                    new Link("КРУТИЛКА", "https://supchat.taxi.yandex-team.ru/settings", "Для письменной поддержки"),
                    new Link("НОК", "https://calltech.yandex-team.ru/form/eda_support/", "Для голосовой поддержки"),
                    new Link("АДМИНКА", "https://admin.eda.yandex-team.ru/", "Админ-панель"),
                    new Link("АДМИНКА ПРОМОКОДОВ", "https://external-admin-proxy.taxi.yandex-team.ru/eats-promocodes/statistics", "Статистика промокодов"),
                    new Link("ТИЧБЕЙЗ", "https://go.teachbase.ru/course_sessions/active", "Платформа для курсов и тестов"),
                    new Link("Черная почта", "https://mail.yandex-team.ru/", "Рабочая почта"),
                    new Link("Агент", "https://agent.yandex-team.ru/", "Платформа для показателей"),
                    new Link("Служебный бот", "tg://resolve?domain=WFMEffratToolsBot", "Бот для перерывов")
            ));
            linkService.addCategory(tools);

            // Формы
            LinkCategory forms = new LinkCategory("Формы", Arrays.asList(
                    new Link("Антифрод", "https://forms.yandex-team.ru/surveys/61763/", "Проверка подозрительных пользователей"),
                    new Link("Техническая поддержка", "https://forms.yandex-team.ru/surveys/147372/", "Технические проблемы клиентов")
            ));
            linkService.addCategory(forms);

            // Жалобы
            LinkCategory complaints = new LinkCategory("Жалобы", Arrays.asList(
                    new Link("На сервис", "https://forms.yandex-team.ru/surveys/84880/", "Жалоба на сервис в целом"),
                    new Link("На агента/оператора", "https://forms.yandex-team.ru/surveys/100079/", "Жалоба на оператора"),
                    new Link("На курьера", "https://forms.yandex-team.ru/surveys/63159/", "Жалоба на курьера")
            ));
            linkService.addCategory(complaints);

            // Трекеры
            LinkCategory trackers = new LinkCategory("Трекеры", Arrays.asList(
                    new Link("Внутренний", "https://st.yandex-team.ru/", "Поиск задач"),
                    new Link("БАГТРЕКЕР", "https://st.yandex-team.ru/dashboard/48135", "Трекер багов")
            ));
            linkService.addCategory(trackers);

            // Дополнительно
            LinkCategory additional = new LinkCategory("Дополнительно", Arrays.asList(
                    new Link("Яндекс Еда", "https://eda.yandex.ru/", "Сайт Яндекс Еда"),
                    new Link("Яндекс Карты", "https://yandex.ru/maps/", "Проверка расстояний"),
                    new Link("Вики для сотрудника", "https://wiki.yandex-team.ru/bst/bst-yango/dlja-sotrudnika-komandy-podderzhki-biznesa/", "Информация для сотрудников"),
                    new Link("Wiki по сектору Обучения", "https://wiki.yandex-team.ru/struktura-sektora-obuchenija-v-operacionnojj-podde/", "Информация об обучении")
            ));
            linkService.addCategory(additional);
        };
    }

    @Bean
    public CommandLineRunner initDictionary() {
        return args -> {
            // Админка
            DictionaryCategory admin = new DictionaryCategory("Админка", Arrays.asList(
                    new DictionaryTerm("НД", "наша доставка"),
                    new DictionaryTerm("МП", "доставка Маркетплейс (силами самого ресторана)"),
                    new DictionaryTerm("ЛПР", "лицо, принимающее решения (зачастую директор заведения)"),
                    new DictionaryTerm("Вендорка", "данные поставщика (ресторана, кафе) (наше приложение)")
            ));
            dictionaryService.addCategory(admin);

            // Заказ
            DictionaryCategory order = new DictionaryCategory("Заказ", Arrays.asList(
                    new DictionaryTerm("АСАП/ASAP", "срочный заказ (как можно скорее)"),
                    new DictionaryTerm("SPECIFIED", "заказ ко времени"),
                    new DictionaryTerm("БК Логист", "заказ, оформленный через приложение Бургер Кинг, доставку выполняют наши курьеры")
            ));
            dictionaryService.addCategory(order);

            // Другие категории можно добавить аналогично
        };
    }
}