--- Filter Settings schema

# --- !Ups

CREATE TABLE filter_settings
(
  guild_id              BIGINT NOT NULL
    CONSTRAINT filter_settings_pkey
    PRIMARY KEY
    CONSTRAINT filter_settings_bot_instances_guild_id_fk
    REFERENCES bot_instances
    ON UPDATE CASCADE ON DELETE CASCADE,
  caps_filter_enabled   BOOLEAN DEFAULT FALSE,
  caps_filter_exemption_level INTEGER DEFAULT 2,
  caps_filter_threshold INTEGER DEFAULT 20,
  links_filter_enabled  BOOLEAN DEFAULT FALSE,
  links_filter_exemption_level INTEGER DEFAULT 2,
  blacklist_filter_enabled BOOLEAN DEFAULT FALSE,
  blacklist_filter_exemption_level INTEGER DEFAULT 2,
  blacklist_filter_words TEXT DEFAULT ''
);

CREATE UNIQUE INDEX filter_settings_guild_id_uindex
  ON filter_settings (guild_id);

# --- !Downs

DROP TABLE filter_settings;