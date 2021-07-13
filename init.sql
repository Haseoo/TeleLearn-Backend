BEGIN;

DROP TABLE IF EXISTS ATTACHMENTS_DATA;
DROP TABLE IF EXISTS ATTACHMENTS;
DROP TABLE IF EXISTS TASK_STUDENT;
DROP TABLE IF EXISTS TASK_SCHEDULE;
DROP TABLE IF EXISTS TASK_LINKS;
DROP TABLE IF EXISTS TASKS;
DROP TABLE IF EXISTS COMMENTS;
DROP TABLE IF EXISTS POSTS;
DROP TABLE IF EXISTS COURSE_STUDENT;
DROP TABLE IF EXISTS COURSES;
DROP TABLE IF EXISTS GLOBAL_NEWS;
DROP TABLE IF EXISTS LEARNING_TIME;
DROP TABLE IF EXISTS MESSAGES;
DROP TABLE IF EXISTS STUDENT_STATS;
DROP TABLE IF EXISTS STUDENTS;
DROP TABLE IF EXISTS TEACHERS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS USER_PASSWORD_CHANGES;

DROP TRIGGER IF EXISTS ATTACHMENT_DATA_TRIGGER ON ATTACHMENTS_DATA;
DROP TRIGGER IF EXISTS USER_PASSWORD_CHANGES_TRIGGER ON USERS;

--TABLES
create table USERS
(
    ID        BIGSERIAL   primary key,
    EMAIL     TEXT        not null,
    ENABLED   BOOLEAN     not null,
    NAME      TEXT        not null,
    PASSWORD  TEXT        not null,
    SURNAME   TEXT        not null,
    USER_ROLE TEXT        not null,
    USERNAME  VARCHAR(255)
);

create table TEACHERS
(
    ID    BIGSERIAL not null primary key,
    TITLE TEXT,
    UNIT  TEXT,

    constraint TEACHER_USER_FK foreign key (ID) references USERS (ID)
);

create table STUDENTS
(
    ID                  BIGSERIAL primary key,
    DAILY_LEARNING_TIME BIGINT not null,
    UNIT                TEXT,

    constraint STUDENT_USER_FK foreign key (ID) references USERS (ID)
);

create table STUDENT_STATS
(
    ID            BIGSERIAL primary key,
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
    ID          BIGSERIAL primary key,
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
    ID               BIGSERIAL primary key,
    BRIEF            TEXT,
    HTML_CONTENT     TEXT,
    PUBLICATION_DATE TIMESTAMP not null,
    TITLE            TEXT      not null,
    AUTHOR_ID        BIGINT    not null,

    constraint NEWS_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID)
);

create table COURSES
(
    ID                        BIGSERIAL primary key,
    AUTO_ACCEPT               BOOLEAN   not null,
    NAME                      TEXT,
    PUBLIC_COURSE             BOOLEAN   not null,
    STUDENTS_ALLOWED_TO_POST  BOOLEAN   not null,
    WELCOME_PAGE_HTML_CONTENT TEXT,
    OWNER_ID                  BIGINT    not null,

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
    ID                 BIGSERIAL  primary key,
    COMMENTING_ALLOWED BOOLEAN    not null,
    CONTENT            TEXT,
    POST_VISIBILITY    INTEGER,
    PUBLICATION_TIME   TIMESTAMP,
    AUTHOR_ID          BIGINT     not null,
    COURSE_ID          BIGINT     not null,

    constraint POST_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID),
    constraint POST_COURSE_FK foreign key (COURSE_ID) references COURSES (ID)
);

create table COMMENTS
(
    ID               BIGSERIAL  primary key,
    CONTENT          TEXT       not null,
    PUBLICATION_TIME TIMESTAMP,
    AUTHOR_ID        BIGINT     not null,
    POST_ID          BIGINT     not null,

    constraint COMMENT_POST_FK foreign key (POST_ID) references POSTS (ID),
    constraint COMMENT_AUTHOR_FK foreign key (AUTHOR_ID) references USERS (ID)
);

create table TASKS
(
    ID            BIGSERIAL primary key,
    DESCRIPTION   TEXT,
    DUE_DATE      DATE      not null,
    LEARNING_TIME BIGINT    not null,
    NAME          TEXT      not null,
    COURSE_ID     BIGINT    not null,

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
    ID            BIGSERIAL primary key,
    DATE          DATE      not null,
    LEARNING_TIME BIGINT    not null,
    PLANNED_TIME  BIGINT    not null,
    SCHEDULE_TIME TIME,
    STUDENT_ID    BIGINT    not null,
    TASK_ID       BIGINT    not null,

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
    ID          BIGSERIAL primary key,
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
    ID            BIGSERIAL primary key,
    ATTACHMENT_ID BIGINT,
    data          OID    not null,
    constraint ATTACHMENT_DATA_FK foreign key (ATTACHMENT_ID) references ATTACHMENTS(ID)
);

CREATE TABLE USERS_PASSWORD_CHANGE(
    USER_ID     BIGINT    NOT NULL,
    CHANGE_TIME TIMESTAMP NOT NULL
);


--ADMIN ACCOUNT
INSERT INTO PUBLIC.USERS (EMAIL, ENABLED, NAME, PASSWORD, SURNAME, USER_ROLE, USERNAME) VALUES ('', true, 'Administrator', '$2a$12$QOpC9AXqeKDBcNMWHFr/oOEjcJqxD1IkbZsvshHsTRvsIsLpVbji6', '', '0', 'admin');


--FUNCTIONS
CREATE OR REPLACE FUNCTION ATTACHMENT_DATA_CHECK()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    dataCount     bigint;
BEGIN
    SELECT count(*) INTO dataCount FROM ATTACHMENTS_DATA ad WHERE ad.ATTACHMENT_ID = NEW.ATTACHMENT_ID;
    IF dataCount > 0 THEN
        RAISE EXCEPTION 'Attachment must have only one payload';
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION ON_PASSWORD_UPDATE()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NEW.PASSWORD <> OLD.PASSWORD THEN
        INSERT INTO USERS_PASSWORD_CHANGE VALUES(OLD.id ,now());
    END IF;
    RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION GET_STUDENT_TASK (
    studentId BIGINT
)
    RETURNS TABLE(ID BIGINT,
                  DESCRIPTION TEXT,
                  DUE_DATE DATE,
                  LEARNING_TIME BIGINT,
                  NAME TEXT,
                  COURSE_ID BIGINT)
    LANGUAGE PLPGSQL
AS
$$
begin
    return query
        SELECT
               t.ID,
               t.DESCRIPTION,
               t.DUE_DATE,
               t.LEARNING_TIME,
               t.NAME,
               t.COURSE_ID
        FROM TASKS t
            JOIN COURSES c ON t.COURSE_ID = c.ID
            JOIN COURSE_STUDENT cs ON cs.COURSE_ID = c.ID
        WHERE cs.STUDENT_ID = studentId AND cs.ACCEPTED;
end;
$$;

CREATE OR REPLACE FUNCTION GET_STUDENT_TASK_TO_SCHEDULE (
    studentId BIGINT
)
    RETURNS TABLE(DATE TEXT,
                  TASK_ID BIGINT[])
    LANGUAGE PLPGSQL
AS
$$
begin
    return query
        SELECT
            CASE
                WHEN t.due_date < now() THEN 'DELAYED'
                WHEN ts.to_repeat THEN 'TO_REPEAT'
                ELSE TO_CHAR(t.due_date, 'dd.MM.yyyy')
            END status,
            ARRAY_AGG(t.id)
        FROM GET_STUDENT_TASK(studentId) t
                 LEFT OUTER JOIN TASK_STUDENT ts ON (t.ID = ts.TASK_ID AND ts.student_id = studentId)
        WHERE ts.task_completion is NULL OR ts.task_completion <> 100 OR ts.to_repeat IS TRUE
    GROUP BY status ORDER BY STATUS DESC;
end;
$$;

CREATE OR REPLACE FUNCTION GET_TIME_FROM_DURATION(duration BIGINT)
    RETURNS JSONB
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    timeSpan INTERVAL;
    hours INT;
    minutes INT;
BEGIN
    timeSpan := MAKE_INTERVAL(mins => (duration / 60000000000)::INT);
    hours := EXTRACT(HOUR FROM timeSpan);
    timeSpan := timeSpan - MAKE_INTERVAL(hours => hours);
    minutes := EXTRACT(MINUTE FROM timeSpan);
    RETURN JSONB_BUILD_OBJECT('hours', hours, 'minutes', minutes);
END;
$$;

CREATE OR REPLACE FUNCTION GET_STUDENT_STATS(studentId bigint, today DATE default NOW()::date)
    RETURNS JSONB
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    weekBeginDate DATE;
    weekEndDate DATE;
    sevenDaysBeginDate DATE;
    plannedTimeForWeek BIGINT;
    learningTimeForWeek BIGINT;
    courseRecords RECORD;
    courseSevenDays JSON;
    courseGrouped JSON;
BEGIN
    weekBeginDate := (today - MAKE_INTERVAL( days => (EXTRACT(ISODOW FROM today))::int))::DATE;
    weekEndDate := (today + MAKE_INTERVAL( days => (8 - EXTRACT(ISODOW FROM today))::int))::DATE;
    sevenDaysBeginDate := today - INTERVAL '7 days';

    SELECT
        COALESCE(AVG(ts.PLANNED_TIME), 0),
        COALESCE(AVG(ts.LEARNING_TIME), 0)
    INTO
        plannedTimeForWeek,
        learningTimeForWeek
    FROM TASK_SCHEDULE ts
    WHERE
            ts.STUDENT_ID = studentId AND
            ts.DATE > weekBeginDate AND
            ts.DATE < weekEndDate;


    SELECT
           c.id as COURSE_ID,
           c.name as COURSE_NAME,
           st.learning_time,
           st.date
    INTO courseRecords
    FROM STUDENT_STATS st
             JOIN COURSES c ON st.COURSE_ID = c.ID;

    SELECT
        JSONB_BUILD_OBJECT
        (
            'course', r.COURSE_NAME,
            'learningTime', GET_TIME_FROM_DURATION(SUM(r.learning_time)::BIGINT)
        )
    INTO courseGrouped
    FROM courseRecords r
    GROUP BY r.COURSE_ID, r.COURSE_NAME;

    SELECT
        JSONB_BUILD_OBJECT
        (
            'course', r.COURSE_NAME,
            'learningTime', GET_TIME_FROM_DURATION(SUM(r.learning_time)::BIGINT)
        )
    INTO courseSevenDays
    FROM courseRecords r
    WHERE
        r.date > sevenDaysBeginDate AND
        r.date <= today
    GROUP BY r.COURSE_ID, r.COURSE_NAME;

    RETURN
        JSONB_BUILD_OBJECT
            (
                'taskTimeForWeek', (
                    SELECT GET_TIME_FROM_DURATION(COALESCE(avg(st.learning_time)::BIGINT, 0))
                    FROM GET_STUDENT_TASK(studentId) st
                    WHERE
                            st.DUE_DATE > weekBeginDate AND
                            st.DUE_DATE < weekEndDate
                ),
                'plannedTimeForWeek', GET_TIME_FROM_DURATION(plannedTimeForWeek),
                'learningTimeForWeek', GET_TIME_FROM_DURATION(learningTimeForWeek),
                'learningTimeForCourseSevenDays', courseSevenDays,
                'learningTimeForCourseTotal', courseGrouped,
                'hoursLearningStats', (
                    SELECT JSONB_AGG(
                                   JSONB_BUILD_OBJECT(
                                           'hour', nested.HOUR,
                                           'LearningTimes', nested.LEARNING_TIMES
                                       )
                               )
                    FROM (
                             SELECT EXTRACT(HOUR FROM st.START_TIME) AS HOUR,
                                    COUNT(*) AS LEARNING_TIMES
                             FROM STUDENT_STATS st
                             WHERE st.STUDENT_ID = studentId
                             GROUP BY EXTRACT(HOUR FROM st.START_TIME)
                         ) nested
                ),
                'averageLearningTime', (
                    SELECT GET_TIME_FROM_DURATION(COALESCE(AVG(st.learning_time)::BIGINT, 0))
                    FROM STUDENT_STATS st
                    WHERE st.STUDENT_ID = studentId
                )
            );
END
$$;


--TRIGGERS
CREATE TRIGGER USER_PASSWORD_CHANGES_TRIGGER
    BEFORE INSERT OR UPDATE
    ON ATTACHMENTS_DATA
    FOR EACH ROW
EXECUTE PROCEDURE ATTACHMENT_DATA_CHECK();

CREATE TRIGGER USER_PASSWORD_CHANGES
    BEFORE UPDATE
    ON USERS
    FOR EACH ROW
EXECUTE PROCEDURE ON_PASSWORD_UPDATE();

COMMIT;
