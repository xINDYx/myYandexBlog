package ru.yandex.blog.repository;

import ru.yandex.blog.model.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findAll(String tag, int offset, int limit);

    void save(Post post);

    void deleteById(Long id);

    Post findById(Long id);

    void updateLikes(Long id, int likesCount);

    void updateComments(Long id, int commentsCount);

    void update(Post post);
}
