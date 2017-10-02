--- Bit Tracking schema

# --- !Ups
CREATE TABLE bit_tracking_settings
(
  guild_id                    BIGINT            NOT NULL,
  current_mode                INTEGER DEFAULT 0 NOT NULL,
  nip_dip_template            TEXT DEFAULT '',
  rbg_template                TEXT DEFAULT '',
  jackshots_template          TEXT DEFAULT '',
  push_up_template            TEXT DEFAULT '',
  sing_it_or_slam_it_template TEXT DEFAULT '',
  CONSTRAINT bit_tracking_settings_bot_instances_guild_id_fk FOREIGN KEY (guild_id) REFERENCES bot_instances (guild_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# -- !Downs

DROP TABLE bit_tracking_settings