CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE
    -- password VARCHAR(255) -- sha256
);

CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE user_group_relations (
    user_id INT,
    group_id INT,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (group_id) REFERENCES groups(id)
);

