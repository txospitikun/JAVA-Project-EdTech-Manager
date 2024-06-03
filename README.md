# REST API CALLS
```
/api/create_professor
Payload:
{
    "jwt": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInByaXZpbGVnZSI6IjMiLCJpYXQiOjE3MTY5NzQ0NDIsImV4cCI6MTcxNzgzODQ0Mn0.Gp4MXNiWFXnYjMiP2akUbJrwXMdoO7EjsxR1PIIDuBjnL12AD_yDecYTISSj14-y6bxLfpksm0VEKksinUP1vw",
    "firstname": "hadambu",
    "lastname": "stelian",
    "rank": "profesor doctor",
    "username": "stelian06",
    "password": "123",
    "courses": [
        "Matematica",
        "Informatica"
    ]
}
Response:
{"Response":"successful"} - 200
Invalid JWT - 401
```

```
/api/login
Payload:
{
    "username":"dennis",
    "password":"123"
}

Response:
{
    "JWT": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJVc2VybmFtZVwiOlwiZGVubmlzXCIsXCJQcml2ZWxlZ2VcIjpcIjBcIn0iLCJpYXQiOjE3MTY5MjUwNDUsImV4cCI6MTcxNzc4OTA0NX0.DwzqrGo51HyJhG-pRaPLrlUnH9-6fMb3s0shTsLiFAGt2jiQbl10TPIcnnXHVCzYuhxjmzd9A5OWKh9sXipCCQ"
}
```

```
/api/register
Payload:
{           "jwt":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJVc2VybmFtZVwiOlwiZGVubmlzXCIsXCJQcml2ZWxlZ2VcIjpcIjBcIn0iLCJpYXQiOjE3MTY5MjUwMjksImV4cCI6MTcxNzc4OTAyOX0.YRRYD8n_s8iaAOWTsRBkTbDiF3yV1QrGSyikpgmm8Eatu2T00SlD94CiW8xrzvbmwQaGxwA4drSVHf7-EYOcwQ",
    "username":"luca",
    "password":"1234"
}

Response:
Inregistrare cu succes:
{"RegisterStatus":"sucessful"}

Inregistrare (user-ul deja exista)
{"RegisterStatus":"user_already_exists"}

Inregistrare (format invalid)
{"RegisterStatus":"user_register_form_invalid"}
```


# SQL TABELS

```
CREATE TABLE Groups (
    ID SERIAL PRIMARY KEY,
    GROUP_NAME VARCHAR NOT NULL UNIQUE,
    YEAR INTEGER DEFAULT EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER,
UNIQUE (YEAR, GROUP_NAME)
);

CREATE TABLE Users (
    ID SERIAL PRIMARY KEY,
    USERNAME VARCHAR NOT NULL UNIQUE,
    PASSWORD VARCHAR NOT NULL,
    PRIVILEGE INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE Grades (
    ID SERIAL PRIMARY KEY,
    ID_PROF INTEGER NOT NULL,
    NR_MATRICOL VARCHAR NOT NULL,
    ID_COURSE INTEGER NOT NULL,
    VALUE SMALLINT CHECK (value >= 1 AND value <= 10),
    NOTATION_DATE DATE
);

CREATE TABLE Courses (
    ID SERIAL PRIMARY KEY,
    COURSE_TITLE VARCHAR NOT NULL UNIQUE,
    YEAR SMALLINT CHECK (year >= 0 AND year <= 9),
    SEMESTER SMALLINT CHECK (semester >= 0 AND semester <= 2),
    CREDITS SMALLINT CHECK (credits >= 0 AND credits <= 10),
);

CREATE TABLE Professors (
    ID SERIAL PRIMARY KEY,
    FIRST_NAME VARCHAR,
    LAST_NAME VARCHAR,
    RANK VARCHAR,
    USER_ID INTEGER,
    FOREIGN KEY (USER_ID) REFERENCES Users(ID)
);

CREATE TABLE Didactic (
    ID SERIAL PRIMARY KEY,
    ID_PROFESSOR INTEGER NOT NULL,
    ID_COURSE INTEGER NOT NULL,
    UNIQUE (ID_PROFESSOR, ID_COURSE)
);

CREATE TABLE Students (
    ID SERIAL PRIMARY KEY,
    USER_ID INTEGER,
    NR_MATRICOL VARCHAR NOT NULL,
    FIRST_NAME VARCHAR,
    LAST_NAME VARCHAR
    FOREIGN KEY (USER_ID) REFERENCES Users(ID)
);

CREATE TABLE GroupProfessorLink (
    id SERIAL NOT NULL,
    prof_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT example_table_pkey PRIMARY KEY (id),
    CONSTRAINT prof_course_group_unique UNIQUE (prof_id, course_id, group_id)
);


CREATE TABLE StudentYears (
    ID SERIAL PRIMARY KEY,
    ID_STUDENT INTEGER NOT NULL,
    YEAR INTEGER CHECK (year >= 1900 AND year <= 2100),
    STUDY_YEAR INTEGER CHECK (study_year >= 1 AND study_year <= 9),
    GROUP_ID INTEGER NOT NULL,
    FOREIGN KEY (ID_STUDENT) REFERENCES Students(ID),
    UNIQUE (ID_STUDENT, YEAR, STUDY_YEAR)
);

CREATE TABLE Announcements (
    ID SERIAL PRIMARY KEY,
    ANNOUNCEMENT_TITLE VARCHAR NOT NULL,
    ANNOUNCEMENT_CONTENT VARCHAR NOT NULL,
    UPLOAD_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Classrooms (
    ID SERIAL PRIMARY KEY,
    Classroom_Name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE Schedule (
    ID SERIAL PRIMARY KEY,
    WEEK_DAY VARCHAR NOT NULL,
    TIME_DAY VARCHAR NOT NULL,
    LINK_ID INTEGER NOT NULL UNIQUE,
    CLASSROOM_ID INTEGER NOT NULL,
FOREIGN KEY(LINK_ID) REFERENCES GroupProfessorLink(id),
FOREIGN KEY(CLASSROOM_ID) REFERENCES Classrooms(ID),
UNIQUE (week_day, time_day, link_id, classroom_id)
);

```

#PL/SQL script (munca de chinez... )
```
CREATE OR REPLACE FUNCTION register_user(
    p_username VARCHAR,
    p_password VARCHAR,
    p_privilege INT
) RETURNS INT AS $$
DECLARE
    v_user_id INT;
BEGIN
    -- Insert the user into the users table
    INSERT INTO users (username, password, privilege)
    VALUES (p_username, encode(digest(p_password, 'sha256'), 'hex'), p_privilege)
    RETURNING id INTO v_user_id;
    
    RETURN v_user_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION register_professor(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_rank VARCHAR,
    p_username VARCHAR,
    p_password VARCHAR
) RETURNS INT AS $$
DECLARE
    v_user_id INT;
    v_professor_id INT;
BEGIN
    -- Register the user
    v_user_id := register_user(p_username, p_password, 2);
    
    IF v_user_id = -1 THEN
        RETURN -1;
    END IF;

    -- Insert the professor into the professors table
    INSERT INTO Professors (first_name, last_name, rank, user_id)
    VALUES (p_first_name, p_last_name, p_rank, v_user_id)
    RETURNING id INTO v_professor_id;
    
    RETURN v_professor_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION register_professor_courses(
    p_professor_id INT,
    p_courses TEXT[]
) RETURNS INT AS $$
DECLARE
    v_course_title TEXT;
    v_course_id INT;
BEGIN
    FOREACH v_course_title IN ARRAY p_courses LOOP
        -- Find the course ID
        SELECT id INTO v_course_id
        FROM courses
        WHERE course_title = v_course_title;

        -- If the course is found, insert into Didactic table
        IF v_course_id IS NOT NULL THEN
            INSERT INTO Didactic (id_professor, id_course)
            VALUES (p_professor_id, v_course_id);
        END IF;
    END LOOP;

    RETURN 1;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_course_id_by_name(p_course_title VARCHAR)
RETURNS INT AS $$
DECLARE
    v_course_id INT;
BEGIN
    SELECT id INTO v_course_id
    FROM Courses
    WHERE course_title = p_course_title;

    RETURN v_course_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN -1;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION get_all_professors();
CREATE OR REPLACE FUNCTION get_all_professors()
RETURNS TABLE (
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    rank VARCHAR,
    user_id INT,
    username VARCHAR,
    courses VARCHAR[]
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.rank, u.id AS user_id, u.username,
           ARRAY_AGG(c.course_title) AS courses
    FROM professors p
    JOIN users u ON p.user_id = u.id
    LEFT JOIN didactic d ON p.id = d.id_professor
    LEFT JOIN courses c ON d.id_course = c.id
    GROUP BY p.id, p.first_name, p.last_name, p.rank, u.id, u.username;
END;
$$ LANGUAGE plpgsql;
```
