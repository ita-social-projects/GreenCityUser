DROP TABLE users_friends;

CREATE TABLE users_friends(
    ID INT NOT NULL AUTO_INCREMENT NOT NULL,
    PRIMARY KEY (ID),
    USER_ID int NOT NULL,
    FRIEND_ID int NOT NULL,
    STATUS VARCHAR NOT NULL,
    CREATED_DATE TIMESTAMP NOT NULL
);

INSERT INTO languages (id, code)
VALUES (1, 'ua');

INSERT INTO achievement_categories(id, name)
VALUES (1, 'Category 1');

INSERT INTO achievements(id, achievement_category_id, condition)
VALUES (1, 1, 10);

INSERT INTO achievements(id, achievement_category_id, condition)
VALUES (2, 1, 10);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (1, '2020-09-30T00:00', 'test@email.com', 0, 'SuperTest', '2020-09-30T00:00',
        'ROLE_USER', 2, 'secret', 10, 'New York',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (2, '2020-09-29T00:00', 'test2@email.com', 0, 'SuperTest2', '2020-09-29T00:00',
        'ROLE_USER', 2, 'secret', 10, 'LA',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   uuid,
                   language_id)
VALUES (3, '2020-09-28T00:00', 'test3@email.com', 0, 'SuperTest3', '2020-09-28T00:00',
        'ROLE_USER', 2, 'secret', 10, 'Chicago', '444e66e8-8daa-4cb0-8269-a8d856e7dd15' ,1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (4, '2020-09-27T00:00', 'test4@email.com', 1, 'SuperTest4', '2020-09-27T00:00',
        'ROLE_USER', 2, 'secret', 10, 'Miami',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   profile_picture,
                   rating,
                   city,
                   language_id)
VALUES (5, '2020-09-26T00:00', 'test5@email.com', 1, 'SuperTest5',
        '2020-09-26T00:00',  'ROLE_USER', 2, 'secret', 'pathToPicture',  10, 'Dallas',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (6, '2020-09-29T00:00', 'test6@email.com', 0, 'SuperTest6', '2020-09-29T00:00',
        'ROLE_USER', 2, 'secret', 8, 'Toronto',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (7, '2020-09-29T00:00', 'test7@email.com', 0, 'SuperTest7', '2020-09-29T00:00',
        'ROLE_USER', 2, 'secret', 10, 'Montreal',1);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id,
                   uuid)
VALUES (8, '2021-03-31T00:00', 'test8@email.com', 0, 'SuperTest8', '2020-09-29T00:00',
        'ROLE_USER', 2, 'secret', 10, 'Montreal',1, '1488');

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id,
                   uuid)
VALUES (9, '2021-03-31T00:00', 'test9@email.com', 0, 'SuperTest9', '2016-09-29T00:00',
        'ROLE_USER', 1, 'secret', 10, 'Liverpool',1, '1489');




INSERT INTO USERS_FRIENDS(user_id, friend_id, status, created_date)
VALUES (1, 2, 'FRIEND', '2020-09-10');

INSERT INTO USERS_FRIENDS(user_id, friend_id, status, created_date)
VALUES (1, 3, 'FRIEND', '2020-09-10');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (1, 4, 'FRIEND', '2020-09-10 21:00:00+02');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (1, 5, 'FRIEND', '2020-09-10 21:00:00+02');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (1, 6, 'FRIEND', '2020-09-10 21:00:00+02');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (1, 7, 'FRIEND', '2020-09-10 21:00:00+02');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (1, 8, 'FRIEND', '2020-09-10 21:00:00+02');




INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (4, 5, 'REQUEST', '2020-09-10 21:00:00+02');

INSERT INTO users_friends(user_id, friend_id, status, created_date)
VALUES (5, 4, 'REQUEST', '2020-09-10 21:00:00+02');


INSERT INTO user_achievements(user_id, achievement_id, achievement_status, notified)
VALUES (1, 1, 'ACTIVE', false);

INSERT INTO user_achievements(user_id, achievement_id, achievement_status, notified)
VALUES (1, 2, 'ACTIVE', false);
