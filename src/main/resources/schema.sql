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
  content text not null,
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON delete CASCADE
);

insert into posts (title, image_url, content, tags, likes_count, comments_count)
values
  ('Заголовок поста 1', 'https://clck.ru/3Fx4i5', 'Текст поста 1', 'tag2', 0, 2),
  ('Заголовок поста 2', 'https://clck.ru/3Fx4jF', 'Текст поста 2', 'tag1, tag3', 0, 1),
  ('Заголовок поста 3', 'https://clck.ru/3Fx4mV', 'Текст поста 3', 'tag4', 0, 1);

INSERT INTO comments (post_id, content)
VALUES
  (1, 'Отличный пост!'),
  (2, 'Спасибо за информацию!'),
  (1, 'Не согласен с автором.'),
  (3, 'Не согласен с автором.');