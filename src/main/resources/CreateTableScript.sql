--Удаляем все таблицы
DROP TABLE IF EXISTS incomes;
DROP TABLE IF EXISTS outcomes;
DROP TABLE IF EXISTS income_category;
DROP TABLE IF EXISTS outcome_category;
DROP TABLE IF EXISTS cash_account;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
--Создаем таблицу ролей пользователей
CREATE TABLE roles
(
    id INT PRIMARY KEY,
    name character varying(20) UNIQUE NOT NULL
);
INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'USER');
-- Создаем таблицу пользователей домашней бухгалтерии
CREATE TABLE users
(
    id SERIAL PRIMARY KEY,
    login character varying(20) UNIQUE NOT NULL, 
    password character varying(100) NOT NULL,
	role_id INT NOT NULL,
	FOREIGN KEY(role_id) REFERENCES roles(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы ролей
);
INSERT INTO users (login, password, role_id) VALUES ('admin', '$2a$10$xpitttb6w.X7KCrsjxYh1.aLfVj51v3wvQO00.r8MZUzkcRQ.kHsW', 1); --Пароль "admin"
INSERT INTO users (login, password, role_id) VALUES ('user', '$2a$10$iH3kQJLuYzPJjFn9MQXuSuw9l0UQjIkK70VVeFkRD8GDKdFuHIURW', 2); -- Пароль "user"
--Создаем таблицу категорий доходов
CREATE TABLE income_category
(
    id SERIAL PRIMARY KEY,
    name character varying(20) NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT UNIQ_IC_NAME_AND_USER UNIQUE (name, user_id),
    CONSTRAINT NAME_MIN_SIZE CHECK(LENGTH(name)>0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы пользователей   
);
--Cоздаем таблицу категорий расходов
CREATE TABLE outcome_category
(
    id SERIAL PRIMARY KEY,
    name character varying(20) NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT UNIQ_OC_NAME_AND_USER UNIQUE (name, user_id),
    CONSTRAINT NAME_MIN_SIZE CHECK(LENGTH(name)>0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы пользователей    
);
--Создаем таблицу счетов
CREATE TABLE cash_account
(
    id SERIAL PRIMARY KEY,
    name character varying(20) NOT NULL, 
    balance decimal(12,2) NOT NULL,
    contain_in_gen_balance boolean NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT UNIQ_CA_NAME_AND_USER UNIQUE (name, user_id),
    CONSTRAINT NAME_MIN_SIZE CHECK(LENGTH(name)>0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы пользователей
);
--Создаем таблицу расходов
CREATE TABLE outcomes
(
    id SERIAL PRIMARY KEY,
    user_id int,
    date timestamp NOT NULL,
    outcome decimal(12, 2) NOT NULL,
    category_id int, 
    cash_account_id int,
    comment character varying(50),
	FOREIGN KEY(user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE, -- Внешний ключ из таблицы пользователей
	FOREIGN KEY(category_id) REFERENCES outcome_category(id) ON UPDATE CASCADE ON DELETE CASCADE, -- Внешний ключ из таблицы категорий
	FOREIGN KEY(cash_account_id) REFERENCES cash_account(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы счетов
);
--Создаем таблицу доходов
CREATE TABLE incomes
(
    id SERIAL PRIMARY KEY,
    user_id int,
    date timestamp NOT NULL,
    income decimal(12, 2) NOT NULL,
    category_id int, 
    cash_account_id int,
    comment character varying(50),
	FOREIGN KEY(user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE, -- Внешний ключ из таблицы пользователей
	FOREIGN KEY(category_id) REFERENCES income_category(id) ON UPDATE CASCADE ON DELETE CASCADE, -- Внешний ключ из таблицы категорий
	FOREIGN KEY(cash_account_id) REFERENCES cash_account(id) ON UPDATE CASCADE ON DELETE CASCADE -- Внешний ключ из таблицы счетов
);