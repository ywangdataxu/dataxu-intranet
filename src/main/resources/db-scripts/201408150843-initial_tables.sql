BEGIN;

CREATE TABLE contact (
    id serial PRIMARY KEY,
    email varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    created_on timestamp NOT NULL DEFAULT current_timestamp,
    updated_on timestamp NOT NULL DEFAULT current_timestamp,
    CONSTRAINT udx_contact_email UNIQUE(email)
);

CREATE TABLE plan (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    description text;
    created_on timestamp NOT NULL DEFAULT current_timestamp,
    updated_on timestamp NOT NULL DEFAULT current_timestamp,
    CONSTRAINT udx_plan_name UNIQUE(name)
);

CREATE TABLE chapter (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    CONSTRAINT udx_chapter_name UNIQUE(name)
);

CREATE TABLE contact_velocity (
    id serial PRIMARY KEY,
    contact_id int NOT NULL REFERENCES contact(id),
    chapter_id int NOT NULL REFERENCES chapter(id),
    velocity int NOT NULL,
    CONSTRAINT unx_contact_chapter UNIQUE(contact_id, chapter_id)
);

CREATE TABLE plan_contact (
    id serial PRIMARY KEY,
    plan_id int NOT NULL REFERENCES plan(id),
    contact_id int NOT NULL REFERENCES contact(id),
    CONSTRAINT unx_plan_contact UNIQUE(plan_id, contact_id)
);

END;