BEGIN;

CREATE TABLE contact (
    id serial PRIMARY KEY,
    email varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    CONSTRAINT udx_contact_email UNIQUE(email)
);

CREATE TABLE plan (
    id serial PRIMARY KEY,
    name varchar(255),
    created_on timestamp NOT NULL DEFAULT current_timestamp,
    updated_on timestamp NOT NULL DEFAULT current_timestamp,
    CONSTRAINT udx_plan_namel UNIQUE(name)
);

END;