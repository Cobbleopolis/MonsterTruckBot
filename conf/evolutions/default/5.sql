--- Twitch Regulars schema

# --- !Ups
CREATE TABLE twitch_regulars
(
  guild_id BIGINT,
  twitch_username VARCHAR(25),
  CONSTRAINT twitch_regulars_bot_instances_guild_id_fk FOREIGN KEY (guild_id) REFERENCES bot_instances (guild_id)
);
CREATE UNIQUE INDEX twitch_regulars_twitch_username_uindex ON twitch_regulars (twitch_username);

# -- !Downs

DROP TABLE twitch_regulars;