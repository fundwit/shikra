CREATE TABLE user (
    id bigint NOT NULL,
    username VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    phone VARCHAR(32) NULL,
    active BOOLEAN NOT NULL,
    salt VARCHAR(64) NOT NULL,

    create_at DATETIME NOT NULL,
    last_update_at DATETIME NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT user_username_unique UNIQUE(username),
    CONSTRAINT user_email_unique UNIQUE(email),
    CONSTRAINT user_phone_unique UNIQUE(phone)
)
;

CREATE TABLE identity(
   id BIGINT NOT NULL,
   user_id BIGINT NOT NULL,
   type varchar(16) NOT NULL,

   credential VARCHAR(256) NULL,
   external_id VARCHAR(256) NULL,

   create_at DATETIME NOT NULL,
   last_update_at DATETIME NOT NULL,

   PRIMARY KEY (id),
   CONSTRAINT identity_userId_type_unique UNIQUE(user_id, type)
)
;