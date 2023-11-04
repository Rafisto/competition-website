CREATE TABLE contests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    is_published BOOLEAN,
    group_id INT,
    contest_problems INT[],
    FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE contest_problems (
    id SERIAL PRIMARY KEY,
    title TEXT,
    contents TEXT,
    use_autograding BOOLEAN,
    use_autograding_answer TEXT,
    deadline TIMESTAMP
);

CREATE TABLE contest_problems_assignment (
    contest_id INT,
    problem_id INT,
    PRIMARY KEY (contest_id, problem_id),
    FOREIGN KEY (contest_id) REFERENCES contests(id),
    FOREIGN KEY (problem_id) REFERENCES contest_problems(id)
);

CREATE TABLE contest_grading (
    user_id INT,
    problem_id INT,
    score INT,
    PRIMARY KEY (user_id, problem_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (problem_id) REFERENCES contest_problems(id)
);
