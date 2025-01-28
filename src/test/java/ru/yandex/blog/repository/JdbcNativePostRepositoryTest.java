package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.blog.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcNativePostRepositoryTest {

    private JdbcNativePostRepository postRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        postRepository = new JdbcNativePostRepository(jdbcTemplate);
    }

    @Test
    void testFindAll_NoTag() {
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts LIMIT ? OFFSET ?";
        Post expectedPost = new Post(1L, "Title", "image.jpg", "Content", "tag1,tag2", 10, 5);

        when(jdbcTemplate.query(
                eq(sql),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<Post>>any()
        )).thenReturn(List.of(expectedPost));

        List<Post> posts = postRepository.findAll(null, 0, 10);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Title", posts.get(0).getTitle());
    }

    @Test
    void testFindById() {
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts WHERE id = ?";
        Post expectedPost = new Post(1L, "Title", "image.jpg", "Content", "tag1,tag2", 10, 5);

        when(jdbcTemplate.queryForObject(
                eq(sql),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<Post>>any()
        )).thenReturn(expectedPost);

        Post post = postRepository.findById(1L);

        assertNotNull(post);
        assertEquals("Title", post.getTitle());
    }

    @Test
    void testSave() {
        String sql = "INSERT INTO posts(title, image_url, content, tags, likes_count, comments_count) VALUES(?, ?, ?, ?, ?, ?)";
        Post post = new Post(null, "New Title", "new_image.jpg", "New Content", "tag3", 0, 0);

        when(jdbcTemplate.update(eq(sql), any(Object[].class))).thenReturn(1);

        assertDoesNotThrow(() -> postRepository.save(post));
    }

    @Test
    void testUpdateLikes() {
        String sql = "UPDATE posts SET likes_count = ? WHERE id = ?";

        when(jdbcTemplate.update(eq(sql), anyInt(), anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> postRepository.updateLikes(1L, 100));
    }

    @Test
    void testUpdateComments() {
        String sql = "UPDATE posts SET comments_count = ? WHERE id = ?";

        when(jdbcTemplate.update(eq(sql), anyInt(), anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> postRepository.updateComments(1L, 50));
    }

    @Test
    void testUpdate() {
        String sql = "UPDATE posts SET title = ?, image_url = ?, content = ?, tags = ?, likes_count = ?, comments_count = ? WHERE id = ?";
        Post post = new Post(1L, "Updated Title", "updated_image.jpg", "Updated Content", "tag4", 20, 10);

        when(jdbcTemplate.update(eq(sql), anyInt(), anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> postRepository.update(post));
    }

    @Test
    void testDeleteById() {
        String sql = "DELETE FROM posts WHERE id = ?";

        when(jdbcTemplate.update(eq(sql), anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> postRepository.deleteById(1L));
    }
}

