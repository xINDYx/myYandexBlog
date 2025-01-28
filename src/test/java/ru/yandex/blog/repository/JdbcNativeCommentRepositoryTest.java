package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.blog.config.TestDataSourceConfig;
import ru.yandex.blog.model.Comment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDataSourceConfig.class)
class JdbcNativeCommentRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcNativeCommentRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new JdbcNativeCommentRepository(jdbcTemplate);

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("schema-test.sql"));
        }

        jdbcTemplate.update("TRUNCATE TABLE comments RESTART IDENTITY");
    }

    @Test
    void testSave() {
        Comment comment = new Comment(1L, 1L, "Test comment");
        repository.save(comment);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments", Integer.class);
        assertEquals(1, count);
    }

    @Test
    void testFindByPostId() {
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Comment 1')");
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Comment 2')");

        List<Comment> comments = repository.findByPostId(1L);
        assertEquals(2, comments.size());
    }

    @Test
    void testFindById() {
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Test Comment')");
        Comment comment = repository.findById(1L);
        assertNotNull(comment);
        assertEquals("Test Comment", comment.getContent());
    }

    @Test
    void testUpdate() {
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Old Comment')");
        Comment updatedComment = new Comment(1L, 1L, "Updated Comment");
        repository.update(updatedComment);

        String content = jdbcTemplate.queryForObject("SELECT content FROM comments WHERE id = 1", String.class);
        assertEquals("Updated Comment", content);
    }

    @Test
    void testDeleteById() {
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Test Comment')");
        repository.deleteById(1L);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE id = 1", Integer.class);
        assertEquals(0, count);
    }

    @Test
    void testGetPostIdByCommentId() {
        jdbcTemplate.execute("INSERT INTO comments (post_id, content) VALUES (1, 'Test Comment')");
        Long postId = repository.getPostIdByCommentId(1L);
        assertEquals(1L, postId);
    }
}
