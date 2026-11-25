# 📋 Kanban — трекер задач на чистой Java

Трекер задач без фреймворков: задачи, эпики и подзадачи, история просмотров, приоритезация и проверка пересечений по времени. Поверх ядра — встроенный HTTP-сервер с REST API.

> Проект курса «Java-разработчик» Яндекс.Практикума.

## Стек

![Java](https://img.shields.io/badge/Java-17-6DB33F?style=flat&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?style=flat&logo=apachemaven&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5-green?style=flat&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-mocking-78BC6B?style=flat)
![GitHub Actions](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?style=flat&logo=githubactions&logoColor=white)

## Возможности

- Три типа задач: обычные задачи, эпики с подзадачами, расчёт статуса эпика по подзадачам
- Хранение в памяти + `FileBackedTaskManager` с автосохранением состояния в файл
- История просмотров без дубликатов (свой двусвязный список + `HashMap`)
- Приоритезация задач по времени начала, проверка пересечений интервалов
- REST API на `com.sun.net.httpserver`: CRUD для задач/эпиков/подзадач, история, приоритетные задачи

## Структура

```
src/main/java/
├── model/    # Task, Epic, Subtask, статусы и типы
├── manager/  # менеджеры задач: in-memory, file-backed, история
└── http/     # встроенный HTTP-сервер и обработчики эндпоинтов
```

## Запуск

```bash
mvn compile
mvn exec:java -Dexec.mainClass="Main"   # стартует HttpTaskServer на порту 8080
```

## Тесты

14 тест-классов: менеджеры (in-memory и файловый), история, модели, все обработчики HTTP.

```bash
mvn test
```
