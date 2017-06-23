--- Bot Instance schema

# --- !Ups

CREATE TABLE bot_instances
(
  guild_id       BIGINT NOT NULL PRIMARY KEY
);

# --- !Downs

DROP TABLE bot_instances;