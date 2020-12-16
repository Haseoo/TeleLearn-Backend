create table USERS
(
    ID        IDENTITY primary key,
    EMAIL     TEXT    not null,
    ENABLED   BOOLEAN not null,
    NAME      TEXT    not null,
    PASSWORD  TEXT    not null,
    SURNAME   TEXT    not null,
    USER_ROLE TEXT    not null,
    USERNAME  VARCHAR(255)
);

create table TEACHERS
(
    ID    IDENTITY not null primary key,
    TITLE TEXT,
    UNIT  TEXT,

    constraint TEACHER_USER_FK foreign key (ID) references USERS (ID)
);

create table STUDENTS
(
    ID                  IDENTITY primary key,
    DAILY_LEARNING_TIME BIGINT not null,
    UNIT                TEXT,

    constraint STUDENT_USER_FK foreign key (ID) references USERS (ID)
);

create table STUDENT_STATS
(
    ID            IDENTITY primary key,
    COURSE_ID     BIGINT not null,
    DATE          DATE   not null,
    LEARNING_TIME BIGINT not null,
    SCHEDULE_ID   BIGINT not null,
    START_TIME    TIME   not null,
    STUDENT_ID    BIGINT not null,

    constraint STUDENT_STAT_FK foreign key (STUDENT_ID) references STUDENTS (ID)
);

create table MESSAGES
(
    ID          IDENTITY primary key,
    CONTENT     TEXT      not null,
    READ        BOOLEAN   not null,
    SEND_TIME   TIMESTAMP not null,
    RECEIVER_ID BIGINT    not null,
    SENDER_ID   BIGINT    not null,

    constraint MESSAGE_SENDER_FK foreign key (SENDER_ID) references USERS (ID),
    constraint MESSAGE_RECEIVER_FK foreign key (RECEIVER_ID) references USERS (ID)
);

create table LEARNING_TIME
(
    DATE       DATE   not null,
    STUDENT_ID BIGINT not null,
    TIME       BIGINT not null,

    primary key (DATE, STUDENT_ID),
    constraint LEARNING_TIME_STUDENT_FK foreign key (STUDENT_ID) references STUDENTS (ID)
);

create table GLOBAL_NEWS
(
    ID               IDENTITY primary key,
    BRIEF            TEXT,
    HTML_CONTENT     TEXT,
    PUBLICATION_DATE TIMESTAMP not null,
    TITLE            TEXT      not null,
    AUTHOR_ID        BIGINT    not null,

    constraint NEWS_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID)
);

create table COURSES
(
    ID                        IDENTITY primary key,
    AUTO_ACCEPT               BOOLEAN not null,
    NAME                      TEXT,
    PUBLIC_COURSE             BOOLEAN not null,
    STUDENTS_ALLOWED_TO_POST  BOOLEAN not null,
    WELCOME_PAGE_HTML_CONTENT TEXT,
    OWNER_ID                  BIGINT  not null,

    constraint COURSE_TEACHER_FK foreign key (OWNER_ID) references TEACHERS (ID)
);

create table COURSE_STUDENT
(
    ACCEPTED   BOOLEAN not null,
    STUDENT_ID BIGINT  not null,
    COURSE_ID  BIGINT  not null,

    primary key (COURSE_ID, STUDENT_ID),
    constraint CS_STUDENT_FK foreign key (STUDENT_ID) references STUDENTS (ID),
    constraint CS_COURSE_FK foreign key (COURSE_ID) references COURSES (ID)
);

create table POSTS
(
    ID                 IDENTITY primary key,
    COMMENTING_ALLOWED BOOLEAN not null,
    CONTENT            TEXT,
    POST_VISIBILITY    INTEGER,
    PUBLICATION_TIME   TIMESTAMP,
    AUTHOR_ID          BIGINT  not null,
    COURSE_ID          BIGINT  not null,

    constraint POST_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID),
    constraint POST_COURSE_FK foreign key (COURSE_ID) references COURSES (ID)
);

create table COMMENTS
(
    ID               IDENTITY primary key,
    CONTENT          TEXT   not null,
    PUBLICATION_TIME TIMESTAMP,
    AUTHOR_ID        BIGINT not null,
    POST_ID          BIGINT not null,

    constraint COMMENT_POST_FK foreign key (POST_ID) references POSTS (ID),
    constraint COMMENT_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID)
);

create table TASKS
(
    ID            IDENTITY primary key,
    DESCRIPTION   TEXT,
    DUE_DATE      DATE   not null,
    LEARNING_TIME BIGINT not null,
    NAME          TEXT   not null,
    COURSE_ID     BIGINT not null,

    constraint TASK_COURSE_FK foreign key (COURSE_ID) references COURSES (ID)
);

create table TASK_LINKS
(
    TASK_ID          BIGINT not null,
    PREVIOUS_TASK_ID BIGINT not null,

    primary key (TASK_ID, PREVIOUS_TASK_ID),
    constraint TASK_LINK_FK foreign key (TASK_ID) references TASKS (ID),
    constraint PREV_TASK_LINK_FK foreign key (PREVIOUS_TASK_ID) references TASKS (ID)
);

create table TASK_SCHEDULE
(
    ID            IDENTITY primary key,
    DATE          DATE   not null,
    LEARNING_TIME BIGINT not null,
    PLANNED_TIME  BIGINT not null,
    SCHEDULE_TIME TIME,
    STUDENT_ID    BIGINT not null,
    TASK_ID       BIGINT not null,

    constraint SCHEDULE_TASK_FK foreign key (TASK_ID) references TASKS (ID),
    constraint SCHEDULE_STUDENT_FK foreign key (STUDENT_ID) references STUDENTS (ID)
);


create table TASK_STUDENT
(
    TASK_COMPLETION INTEGER not null,
    TO_REPEAT       BOOLEAN not null,
    TASK_ID         BIGINT  not null,
    STUDENT_ID      BIGINT  not null,

    primary key (STUDENT_ID, TASK_ID),
    constraint TS_STUDENT_FK foreign key (STUDENT_ID) references STUDENTS (ID),
    constraint TS_TASK_FK foreign key (TASK_ID) references TASKS (ID)
);

create table ATTACHMENTS
(
    ID          IDENTITY primary key,
    FILE_NAME   TEXT      not null,
    FILE_TYPE   TEXT,
    UPLOAD_TIME TIMESTAMP not null,
    POST_ID     BIGINT,
    TASK_ID     BIGINT,

    constraint ATTACHMENT_POST_FK foreign key (POST_ID) references POSTS (ID),
    constraint ATTACHMENT_TASK_FK foreign key (TASK_ID) references TASKS (ID)
);

create table ATTACHMENTS_DATA
(
    ID            IDENTITY primary key,
    ATTACHMENT_ID BIGINT,
    data          BLOB not null,
    constraint ATTACHMENT_DATA_FK foreign key (ATTACHMENT_ID) references ATTACHMENTS (ID)
);

CREATE TABLE USERS_PASSWORD_CHANGE
(
    USER_ID     BIGINT    NOT NULL,
    CHANGE_TIME TIMESTAMP NOT NULL
);