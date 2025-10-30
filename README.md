# Telegram-Бот для Информационной Поддержки Сотрудников

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green?style=flat-square)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-orange?style=flat-square)
![Telegram API](https://img.shields.io/badge/Telegram%20API-java--telegram--bot--api-yellow?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

---

## Описание проекта

Telegram-бот для **ООО «Деливери Клаб»**, предназначенный для автоматизации внутренних процессов и информационной поддержки сотрудников.  
Реализован на **Java 17** с использованием **Spring Boot 3.0**, **MongoDB** и библиотеки `java-telegram-bot-api`.

Бот обеспечивает:
- Управление пользователями (создание, роли)
- Подачу и модерацию запросов (предложения, больничные, задачи)
- Доступ к базе знаний с поиском
- Просмотр графиков смен
- Загрузку и модерацию файлов
- Ролевую модель: **сотрудник**, **модератор**, **администратор**

---

## Требования

| Компонент             | Версия         |
|-----------------------|----------------|
| Java                  | 17+            |
| Spring Boot           | 3.0+           |
| MongoDB               | 7.0+           |
| Maven                 | 3.8+           |
| Telegram Bot Token    | от @BotFather  |

---

## Установка и запуск

### 1. Клонирование репозитория
```bash
git clone https://github.com/your-username/delivery-club-helpbot.git
cd delivery-club-helpbot
```
Создайте или отредактируйте файл src/main/resources/application.properties:
properties# Токен бота (получите у @BotFather)
telegram.bot.token=ВАШ_ТОКЕН_ЗДЕСЬ

# Подключение к MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/deliveryclub_bot

# Папка для загрузки файлов
app.upload.dir=./uploads

