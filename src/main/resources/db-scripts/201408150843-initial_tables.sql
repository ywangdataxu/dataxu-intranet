BEGIN;

CREATE TABLE contact (
    id serial PRIMARY KEY,
    email varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    CONSTRAINT udx_contact_email UNIQUE(email)
);


END;