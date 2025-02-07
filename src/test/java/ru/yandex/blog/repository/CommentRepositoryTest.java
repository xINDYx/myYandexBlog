package ru.yandex.blog.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.blog.model.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testSaveAndFindByPostId() {
        Comment comment = new Comment(null, 1L, "Test content");
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByPostId(1L);

        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).getContent()).isEqualTo("Test content");
    }

    @Test
    void testDeleteById() {
        Comment comment = new Comment(null, 2L, "To be deleted");
        commentRepository.save(comment);

        commentRepository.deleteById(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void testGetPostIdByCommentId() {
        Comment comment = new Comment(null, 3L, "Post ID fetch test");
        commentRepository.save(comment);

        Long postId = commentRepository.getPostIdByCommentId(comment.getId());

        assertThat(postId).isEqualTo(3L);
    }
}
