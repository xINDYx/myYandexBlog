-- Таблица с постами
create table if not exists posts(
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title varchar(256) not null,
  image_url varchar(512),
  content text,
  tags varchar(512),
  likes_count integer not null default 0,
  comments_count integer not null default 0
);

-- Таблица с коментариями
create table if not exists comments(
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  post_id BIGINT not null,
  content text not null
);