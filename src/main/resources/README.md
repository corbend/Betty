#ЗАПУСК ПРИМЕРА ВНЕ КОНТЕЙНЕРА

1 - Создать базу Betty через pgAdmin
2 - Запустить команду mvn clean verify exec:java
3 - Увидеть в консоли сообщения

#ЗАПУСК ПРИМЕРА В КОНТЕЙНЕРЕ

1 - создание в панели администратора jdbcPool
  url = jdbc:postgresql://localhost:5432/Betty

2 - настройка TimerService в Postgres
    http://stackoverflow.com/questions/14934920/postgresql-and-glassfish-ejb-timer-tbl-table
    CREATE TABLE public."EJB__TIMER__TBL"
    (
      "CREATIONTIMERAW" bigint NOT NULL,
      "BLOB" bytea,
      "TIMERID" character varying(255) NOT NULL,
      "CONTAINERID" bigint NOT NULL,
      "OWNERID" character varying(255) NULL,
      "STATE" integer NOT NULL,
      "PKHASHCODE" integer NOT NULL,
      "INTERVALDURATION" bigint NOT NULL,
      "INITIALEXPIRATIONRAW" bigint NOT NULL,
      "LASTEXPIRATIONRAW" bigint NOT NULL,
      "SCHEDULE" character varying(255) NULL,
      "APPLICATIONID" bigint NOT NULL,

      CONSTRAINT "PK_EJB__TIMER__TBL" PRIMARY KEY ("TIMERID")
    )
    WITH (
      OIDS=FALSE
    );
    ALTER TABLE "EJB__TIMER__TBL"
      OWNER TO postgres;

TODO

#ПЛАН ПРОЕКТА

1 - Создать рабочий пример для тестирования работы EJB вне контейнера Glassfish
 Пример включает в себя одну сущность Game (описание типа спортивной игры) и EJB для работы с ней
2 - Создать полноценный пример, включающий JSF страницу и логику добавления сущностей игр
3 - Регистрация нового аккаунта пользователя и редактирование информации пользователя
4 - Создание фейковых денежных транзакций на аккаунте пользователя
5 - Добавление ставок игр в онлайн режиме администратором системы (необходимый CRUD)
6 - Возможность сделать онлайновую ставку (просмотр активных ставок, списание средств с аккаунта)
7 - Обсчет результатов игр и разрешение выигрышей (зачисление и списание средств, закрытие активных ставок)
8 - Парсинг расписаний игровых событий с крупных спортивных сайтов
9 - Парсинг результатов игровых событий и запуск обсчета результатов (batch-processing)

10 - Изменение активных ставок в реальном времени и уведомление клиентов через WebSockets
11 - Хранение активных ставок и игровых событий в Redis
12 - Интеграция с платежными сервисами и тестирование возможности демо-счета
13 - Рассылка уведомлений по почте об системных событиях для пользователей и администраторов
14 - Статистика администратора и различные настройки системы через панель администратора
15 - Пополнение счета с телефона и Push уведомления
16 - Покрытие тестами, логирование важных системых событий и введение системы в Continious Integration
17 - Тестирование отказоустойчивости на примере создание реплик
18 - Sharding and Partitioning
19 - Развертывание системы и бета-тестирование
20 - Создание лендинга и запуск системы в эксплуатацию
