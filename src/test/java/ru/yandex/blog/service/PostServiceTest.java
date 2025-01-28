package ru.yandex.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.config.TestDataSourceConfig;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDataSourceConfig.class)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("schema-test.sql"));
        }

        jdbcTemplate.update("TRUNCATE TABLE posts RESTART IDENTITY");
        jdbcTemplate.update("TRUNCATE TABLE comments RESTART IDENTITY");

        testPost = new Post(1L, "Test Post", "Test image", "Content of the test post", "test tags", 0, 0);
        postRepository.save(testPost);

        testComment = new Comment(1L, testPost.getId(), "Test comment");
        commentRepository.save(testComment);
    }

    @Test
    void testFindAll() {
        List<Post> posts = postService.findAll(null, 0, 10);
        assertNotNull(posts);
        assertTrue(posts.size() > 0);
    }

    @Test
    void testGetPostById() {
        Post foundPost = postService.getPostById(testPost.getId());
        assertNotNull(foundPost);
        assertEquals(testPost.getId(), foundPost.getId());
        assertEquals(testPost.getTitle(), foundPost.getTitle());
    }

    @Test
    void testGetCommentsByPostId() {
        List<Comment> comments = postService.getCommentsByPostId(testPost.getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(testComment.getContent(), comments.get(0).getContent());
    }

    @Test
    void testAddComment() {
        postService.addComment(testPost.getId(), "New Comment");
        List<Comment> comments = postService.getCommentsByPostId(testPost.getId());
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("New Comment", comments.get(1).getContent());
    }

    @Test
    void testUpdatePost() {
        Post updatedPost = new Post(testPost.getId(), "Updated Title", "Updated image","Updated content", "updated tags", 0, 0);
        postService.updatePost(testPost.getId(), updatedPost);
        Post foundPost = postService.getPostById(testPost.getId());
        assertNotNull(foundPost);
        assertEquals(updatedPost.getTitle(), foundPost.getTitle());
        assertEquals(updatedPost.getContent(), foundPost.getContent());
    }

    @Test
    void testLikePost() {
        int initialLikes = testPost.getLikesCount();
        postService.likePost(testPost.getId());
        Post foundPost = postService.getPostById(testPost.getId());
        assertNotNull(foundPost);
        assertEquals(initialLikes + 1, foundPost.getLikesCount());
    }

    @Test
    void testIncreaseCommentCount() {
        int initialCommentCount = testPost.getCommentsCount();
        postService.increaseCommentCount(testPost.getId());
        Post foundPost = postService.getPostById(testPost.getId());
        assertNotNull(foundPost);
        assertEquals(initialCommentCount + 1, foundPost.getCommentsCount());
    }

    @Test
    void testDecreaseCommentCount() {
        int initialCommentCount = testPost.getCommentsCount();
        postService.decreaseCommentCount(testPost.getId());
        Post foundPost = postService.getPostById(testPost.getId());
        assertNotNull(foundPost);
        assertEquals(initialCommentCount - 1, foundPost.getCommentsCount());
    }

    @Test
    void testGetShortContent() {
        String content = "First paragraph.\n\nSecond paragraph.\nThird paragraph.";
        String shortContent = postService.getShortContent(content);
        assertNotNull(shortContent);
        assertTrue(shortContent.contains("First paragraph."));
        assertFalse(shortContent.contains("Second paragraph."));
    }
}
