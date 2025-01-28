package ru.yandex.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.config.TestDataSourceConfig;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.JdbcNativeCommentRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDataSourceConfig.class)
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcNativeCommentRepository repository;

    private Comment testComment;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new JdbcNativeCommentRepository(jdbcTemplate);

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("schema-test.sql"));
        }

        jdbcTemplate.update("TRUNCATE TABLE comments RESTART IDENTITY");

        testComment = new Comment(1L, 1L, "Test comment");
        commentRepository.save(testComment);
    }

    @Test
    void testFindById() {
        Comment foundComment = commentService.findById(testComment.getId());
        assertNotNull(foundComment);
        assertEquals(testComment.getContent(), foundComment.getContent());
    }

    @Test
    void testUpdateComment() {
        testComment.setContent("Updated comment");
        commentService.updateComment(testComment);

        Comment updatedComment = commentRepository.findById(testComment.getId());
        assertEquals("Updated comment", updatedComment.getContent());
    }

    @Test
    void testGetPostIdByCommentId() {
        Long postId = commentService.getPostIdByCommentId(testComment.getId());

        assertNotNull(postId);
        assertEquals(testComment.getPostId(), postId);
    }
}