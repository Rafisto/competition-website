CREATE TABLE contests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    is_published BOOLEAN
);

CREATE TABLE contest_groups_relations (
    contest_id INT,
    group_id INT,
    PRIMARY KEY (contest_id, group_id),
    FOREIGN KEY (contest_id) REFERENCES contests(id),
    FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE contest_user_relations (
    contest_id INT,
    user_id INT,
    PRIMARY KEY (contest_id, user_id),
    FOREIGN KEY (contest_id) REFERENCES contests(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE contest_problems (
    id SERIAL PRIMARY KEY,
    title TEXT,
    contents TEXT,
    use_autograding BOOLEAN,
    use_autograding_answer TEXT,
    deadline TIMESTAMP,
    contest_id INT,
    FOREIGN KEY (contest_id) REFERENCES contests(id)
);

-- CREATE TABLE contest_problems_relations (
--     contest_id INT,
--     problem_id INT,
--     PRIMARY KEY (contest_id, problem_id),
--     FOREIGN KEY (contest_id) REFERENCES contests(id),
--     FOREIGN KEY (problem_id) REFERENCES contest_problems(id)
-- );

CREATE TABLE contest_grading (
    user_id INT,
    problem_id INT,
    answer TEXT NOT NULL,
    is_file BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    score INT,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, problem_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (problem_id) REFERENCES contest_problems(id)
);
