--- Custom Commands schema

# --- !Ups

CREATE TABLE custom_commands
(
  guild_id         BIGINT            NOT NULL,
  command_name     TEXT NOT NULL,
  permission_level INTEGER DEFAULT 0 NOT NULL,
  command_content  TEXT DEFAULT '',
  PRIMARY KEY (guild_id, command_name),
  CONSTRAINT custom_commands_bot_instances_guild_id_fk FOREIGN KEY (guild_id) REFERENCES bot_instances (guild_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# -- !Downs

DROP TABLE custom_commands;