--- Core Settings schema

# --- !Ups

CREATE TABLE core_settings
(
  guild_id          BIGINT NOT NULL
    CONSTRAINT core_settings_pkey
    PRIMARY KEY
    CONSTRAINT core_settings_bot_instances_guild_id_fk
    REFERENCES bot_instances,
  moderator_role_id VARCHAR(256)
);

# --- !Downs

DROP TABLE core_settings;