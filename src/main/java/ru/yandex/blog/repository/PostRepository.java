package ru.yandex.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.blog.model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = """
            SELECT id, title, image_url, content, tags, likes_count, comments_count 
            FROM post 
            WHERE (:tag IS NULL OR tags LIKE %:tag%) 
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Post> findAll(@Param("tag") String tag, @Param("offset") int offset, @Param("limit") int limit);

    void deleteById(Long id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Post p SET p.likesCount = :likesCount WHERE p.id = :id")
    void updateLikes(Long id, int likesCount);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Post p SET p.commentsCount = :commentsCount WHERE p.id = :id")
    void updateComments(Long id, int commentsCount);
}
