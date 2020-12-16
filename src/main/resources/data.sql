INSERT INTO public.users (id, enabled, password, username, name, surname, user_role, email)
VALUES (1, true, '$2a$12$QOpC9AXqeKDBcNMWHFr/oOEjcJqxD1IkbZsvshHsTRvsIsLpVbji6', 'admin', 'Administrator', '', 0, '');
INSERT INTO public.users (id, enabled, password, username, name, surname, user_role, email)
VALUES (2, true, '$2a$10$9PxaMwJKB/aZllRiBYkM1ucSG55Gl1FdPMhipdw2CazKrEGpIipbq', 'nauczyciel', 'Anna', 'Nauczycielska',
        1, 'anna@teach.com');
INSERT INTO public.teachers(title, unit, id)
VALUES ('dr inż.', 'taka sobie szkoła', 2);
INSERT INTO public.users (id, enabled, password, username, name, surname, user_role, email)
VALUES (3, true, '$2a$10$9PxaMwJKB/aZllRiBYkM1ucSG55Gl1FdPMhipdw2CazKrEGpIipbq', 'uczen', 'Piotr', 'Uczniowski', 2,
        'piotr@student.com');
INSERT INTO public.students(unit, id, daily_learning_time)
VALUES ('taka sobie szkoła', 3, 12600000000000);
INSERT INTO public.users (id, enabled, password, username, name, surname, user_role, email)
VALUES (4, true, '$2a$10$9PxaMwJKB/aZllRiBYkM1ucSG55Gl1FdPMhipdw2CazKrEGpIipbq', 'nauczyciel2', 'Tomasz', 'Problem', 1,
        't.problem@teach.com');
INSERT INTO public.teachers(title, unit, id)
VALUES ('dr inż.', 'taka sobie szkoła', 4);
INSERT INTO public.users (id, enabled, password, username, name, surname, user_role, email)
VALUES (5, true, '$2a$10$9PxaMwJKB/aZllRiBYkM1ucSG55Gl1FdPMhipdw2CazKrEGpIipbq', 'uczen2', 'Atoszka', 'Nagranuszku', 2,
        'antoszka@student.com');
INSERT INTO public.students(unit, id, daily_learning_time)
VALUES ('taka sobie szkoła', 5, 12600000000000);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (1, 'wiadomosc', false, {ts '2020-09-17 18:47:52.69'}, 2, 3);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (2, 'wiadomosc2', false, {ts '2020-09-17 18:47:53.69'}, 3, 2);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (3, 'wiadomosc3', false, {ts '2020-09-17 18:48:52.69'}, 2, 3);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (4, 'wiadomosc4', false, {ts '2020-09-17 18:49:53.69'}, 3, 2);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (5, 'wiadomosc5', true, {ts '2020-09-17 18:49:53.69'}, 2, 1);
INSERT INTO public.messages (id, content, read, send_time, receiver_id, sender_id)
values (6, 'wiadomosc6', false, {ts '2020-09-17 19:49:53.69'}, 1, 2);
INSERT INTO public.global_news (id, brief, publication_date, title, author_id)
values (1,
        'Quisque interdum, tellus eu faucibus posuere, urna ipsum dapibus diam, nec porttitor dui mauris eget risus. Praesent mattis mi vitae ligula pellentesque pretium. Suspendisse potenti. Donec non condimentum nibh. Sed efficitur mi tristique, finibus nisi sit amet, pharetra magna. Vestibulum quis faucibus magna. Aenean sit amet porta nunc, viverra maximus nisi. Nullam sed diam id sem ornare laoreet vitae nec nisi. Nam mollis diam ut urna eleifend, et pellentesque ante luctus. Integer luctus, risus nec vulputate condimentum, justo massa ornare massa, vel tincidunt tortor leo vitae ex. Praesent ipsum dui, viverra in sodales a, consequat ut ante. Etiam semper, justo quis congue luctus, nulla diam ultrices ante, id tempus ligula lectus at sapien.',
        {ts '2020-09-17 19:49:53.69'}, 'Artykuł', 1);
INSERT INTO public.global_news (id, brief, publication_date, title, author_id)
values (2,
        'Quisque interdum, tellus eu faucibus posuere, urna ipsum dapibus diam, nec porttitor dui mauris eget risus. Praesent mattis mi vitae ligula pellentesque pretium. Suspendisse potenti. Donec non condimentum nibh. Sed efficitur mi tristique, finibus nisi sit amet, pharetra magna. Vestibulum quis faucibus magna. Aenean sit amet porta nunc, viverra maximus nisi. Nullam sed diam id sem ornare laoreet vitae nec nisi. Nam mollis diam ut urna eleifend, et pellentesque ante luctus. Integer luctus, risus nec vulputate condimentum, justo massa ornare massa, vel tincidunt tortor leo vitae ex. Praesent ipsum dui, viverra in sodales a, consequat ut ante. Etiam semper, justo quis congue luctus, nulla diam ultrices ante, id tempus ligula lectus at sapien.',
        {ts '2020-09-18 19:49:53.69'}, 'Artykuł', 1);
INSERT INTO public.global_news (id, brief, publication_date, title, author_id)
values (3,
        'Quisque interdum, tellus eu faucibus posuere, urna ipsum dapibus diam, nec porttitor dui mauris eget risus. Praesent mattis mi vitae ligula pellentesque pretium. Suspendisse potenti. Donec non condimentum nibh. Sed efficitur mi tristique, finibus nisi sit amet, pharetra magna. Vestibulum quis faucibus magna. Aenean sit amet porta nunc, viverra maximus nisi. Nullam sed diam id sem ornare laoreet vitae nec nisi. Nam mollis diam ut urna eleifend, et pellentesque ante luctus. Integer luctus, risus nec vulputate condimentum, justo massa ornare massa, vel tincidunt tortor leo vitae ex. Praesent ipsum dui, viverra in sodales a, consequat ut ante. Etiam semper, justo quis congue luctus, nulla diam ultrices ante, id tempus ligula lectus at sapien.',
        {ts '2020-09-19 19:49:53.69'}, 'Artykuł', 1);
INSERT INTO public.global_news (id, brief, publication_date, title, author_id)
values (4,
        'Quisque interdum, tellus eu faucibus posuere, urna ipsum dapibus diam, nec porttitor dui mauris eget risus. Praesent mattis mi vitae ligula pellentesque pretium. Suspendisse potenti. Donec non condimentum nibh. Sed efficitur mi tristique, finibus nisi sit amet, pharetra magna. Vestibulum quis faucibus magna. Aenean sit amet porta nunc, viverra maximus nisi. Nullam sed diam id sem ornare laoreet vitae nec nisi. Nam mollis diam ut urna eleifend, et pellentesque ante luctus. Integer luctus, risus nec vulputate condimentum, justo massa ornare massa, vel tincidunt tortor leo vitae ex. Praesent ipsum dui, viverra in sodales a, consequat ut ante. Etiam semper, justo quis congue luctus, nulla diam ultrices ante, id tempus ligula lectus at sapien.',
        {ts '2020-09-11 19:49:53.69'}, 'Artykuł', 1);
INSERT INTO public.global_news (id, brief, publication_date, title, author_id)
values (5,
        'Quisque interdum, tellus eu faucibus posuere, urna ipsum dapibus diam, nec porttitor dui mauris eget risus. Praesent mattis mi vitae ligula pellentesque pretium. Suspendisse potenti. Donec non condimentum nibh. Sed efficitur mi tristique, finibus nisi sit amet, pharetra magna. Vestibulum quis faucibus magna. Aenean sit amet porta nunc, viverra maximus nisi. Nullam sed diam id sem ornare laoreet vitae nec nisi. Nam mollis diam ut urna eleifend, et pellentesque ante luctus. Integer luctus, risus nec vulputate condimentum, justo massa ornare massa, vel tincidunt tortor leo vitae ex. Praesent ipsum dui, viverra in sodales a, consequat ut ante. Etiam semper, justo quis congue luctus, nulla diam ultrices ante, id tempus ligula lectus at sapien.',
        {ts '2020-09-12 19:49:53.69'}, 'Artykuł', 1);
INSERT INTO public.courses (id, AUTO_ACCEPT, NAME, PUBLIC_COURSE, STUDENTS_ALLOWED_TO_POST, WELCOME_PAGE_HTML_CONTENT,
                            OWNER_ID)
VALUES (1, true, 'Testowy kurs', true, true, '<center><b>Witaj w kursie</b></center>', 2);
INSERT INTO public.course_student (accepted, student_id, course_id)
VALUES (true, 3, 1);
INSERT INTO PUBLIC.TASKS (ID, DESCRIPTION, DUE_DATE, LEARNING_TIME, NAME, COURSE_ID)
VALUES (1, 'Test', '2020-11-17', 0, 'zadanie1', 1);
INSERT INTO PUBLIC.TASKS (ID, DESCRIPTION, DUE_DATE, LEARNING_TIME, NAME, COURSE_ID)
VALUES (2, null, '2020-11-26', 1800000000000, 'zadanie2', 1);
INSERT INTO PUBLIC.TASKS (ID, DESCRIPTION, DUE_DATE, LEARNING_TIME, NAME, COURSE_ID)
VALUES (3, null, '2020-11-26', 1800000000000, 'zadanie3', 1);
INSERT INTO PUBLIC.TASKS (ID, DESCRIPTION, DUE_DATE, LEARNING_TIME, NAME, COURSE_ID)
VALUES (4, null, '2020-11-27', 1800000000000, 'zadanie4', 1);
INSERT INTO PUBLIC.TASKS (ID, DESCRIPTION, DUE_DATE, LEARNING_TIME, NAME, COURSE_ID)
VALUES (5, null, '2020-11-28', 1800000000000, 'zadanie5', 1);
INSERT INTO PUBLIC.TASK_LINKS (TASK_ID, PREVIOUS_TASK_ID)
VALUES (2, 1);
INSERT INTO PUBLIC.TASK_LINKS (TASK_ID, PREVIOUS_TASK_ID)
VALUES (3, 2);
INSERT INTO PUBLIC.TASK_LINKS (TASK_ID, PREVIOUS_TASK_ID)
VALUES (4, 2);
INSERT INTO PUBLIC.TASK_LINKS (TASK_ID, PREVIOUS_TASK_ID)
VALUES (5, 3);
INSERT INTO PUBLIC.TASK_LINKS (TASK_ID, PREVIOUS_TASK_ID)
VALUES (5, 4);
INSERT INTO PUBLIC.POSTS (ID, COMMENTING_ALLOWED, CONTENT, POST_VISIBILITY, PUBLICATION_TIME, AUTHOR_ID, COURSE_ID)
VALUES (1, true, 'Testowy post', 0, '2020-11-18 18:31:44.100628', 2, 1);
INSERT INTO PUBLIC.COMMENTS (ID, CONTENT, PUBLICATION_TIME, AUTHOR_ID, POST_ID)
VALUES (1, 'K1', '2020-11-18 18:31:48.430235', 2, 1);
INSERT INTO PUBLIC.COMMENTS (ID, CONTENT, PUBLICATION_TIME, AUTHOR_ID, POST_ID)
VALUES (2, 'k2', '2020-11-18 18:32:03.538178', 3, 1);
