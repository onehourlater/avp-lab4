(8 Вариант) Сервис рассылки обновлений ленты новостей.
Разработать сервис в котором пользовали могут публиковать новости и отдельно микросервис группирующий и рассылающий по электронной почте все новости опубликованные за последние N минут.
При это если записи не добавляются то отсчет времени не осуществляется.

Как запустить:

Нужен работающие PostgreSQL сервер с данными  postgresql://127.0.0.1:5432/news user: "admin", password: "1234567z"

1. Запускаем микросервис рассылки почты через main() в файле MainMicroService

2. mvn package # собираем проект основного сервиса новостей

3. java -jar target/News-Lab4-1.0-SNAPSHOT.jar --server.port=8181 # Запускаем jar файл.

4. http://localhost:8181/