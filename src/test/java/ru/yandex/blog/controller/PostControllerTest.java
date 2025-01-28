package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.blog.config.TestDataSourceConfig;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.repository.PostRepository;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(TestDataSourceConfig.class)
public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;
    private Post testPost;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("schema-test.sql"));
        }

        jdbcTemplate.update("TRUNCATE TABLE posts RESTART IDENTITY");

        testPost = new Post(1L, "Test Post", "Test image", "Content of the test post", "test tags", 0, 0);
        postRepository.save(testPost);
    }

    @Test
    void testCreatePost() throws Exception {
        mockMvc.perform(post("/posts")
                        .param("title", "Test Post")
                        .param("content", "Content of test post")
                        .param("tags", "test")
                        .param("imageUrl", "test-image.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void testDeletePost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{id}?_method=delete", postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void testEditPost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{id}/edit", postId)
                        .param("title", "Updated Test Post")
                        .param("content", "Updated content of the post")
                        .param("tags", "updated-test")
                        .param("imageUrl", "updated-image.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }

    @Test
    void testLikePost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{id}/like", postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }

    @Test
    void testAddComment() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{id}/comment", postId)
                        .param("content", "Test comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }
}
