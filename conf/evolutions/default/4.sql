--- Bit Tracking schema

# --- !Ups
CREATE TABLE bit_tracking_settings
(
  guild_id                    BIGINT            NOT NULL,
  current_mode                INTEGER DEFAULT 0 NOT NULL,
  bit_game_message            TEXT DEFAULT '',
  bits_message                TEXT DEFAULT '',
  goal_message                TEXT DEFAULT '',
  CONSTRAINT bit_tracking_settings_bot_instances_guild_id_fk FOREIGN KEY (guild_id) REFERENCES bot_instances (guild_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# -- !Downs

DROP TABLE bit_tracking_settings