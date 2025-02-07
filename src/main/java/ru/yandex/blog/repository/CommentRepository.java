package ru.yandex.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.blog.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    void deleteById(Long id);

    @Query("SELECT c.postId FROM Comment c WHERE c.id = :id")
    Long getPostIdByCommentId(Long id);
}
