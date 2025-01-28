package ru.yandex.blog.repository;

import ru.yandex.blog.model.Comment;

import java.util.List;


public interface CommentRepository {
    List<Comment> findByPostId(Long postId);

    Comment findById(Long Id);

    void save(Comment comment);

    void deleteById(Long id);

    Long getPostIdByCommentId(Long id);

    void update(Comment comment);

}
