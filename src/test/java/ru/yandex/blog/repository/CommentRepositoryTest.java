package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.blog.model.Comment;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    private JdbcNativeCommentRepository commentRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentRepository = new JdbcNativeCommentRepository(jdbcTemplate);
    }

    @Test
    void testFindByPostId() {
        Long postId = 1L;
        Comment expectedComment = new Comment(1L, postId, "Test comment");

        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of(expectedComment));

        List<Comment> comments = commentRepository.findByPostId(postId);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Test comment", comments.get(0).getContent());
    }

    @Test
    void testSave() {
        Comment comment = new Comment(1L, 1L, "New comment");
        String sql = "INSERT INTO comments (post_id, content) VALUES (?, ?)";

        commentRepository.save(comment);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(comment.getPostId()), eq(comment.getContent()));
    }

    @Test
    void testUpdate() {
        Comment comment = new Comment(1L, 1L, "Updated comment");
        String sql = "UPDATE comments SET post_id = ?, content = ? WHERE id = ?";

        commentRepository.update(comment);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(comment.getPostId()), eq(comment.getContent()), eq(comment.getId()));
    }

    @Test
    void testDeleteById() {
        Long commentId = 1L;

        commentRepository.deleteById(commentId);

        verify(jdbcTemplate, times(1)).update(eq("delete from comments where id = ?"), eq(commentId));
    }
}
