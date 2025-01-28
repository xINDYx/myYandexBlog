package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.blog.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest {

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

        when(jdbcTemplate.query(eq(sql), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of(expectedPost));

        List<Post> posts = postRepository.findAll(null, 0, 10);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Title", posts.get(0).getTitle());
    }

    @Test
    void testFindAll_WithTag() {
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts WHERE tags LIKE ? LIMIT ? OFFSET ?";
        Post expectedPost = new Post(1L, "Title", "image.jpg", "Content", "tag1,tag2", 10, 5);

        when(jdbcTemplate.query(eq(sql), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of(expectedPost));

        List<Post> posts = postRepository.findAll("tag1", 0, 10);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("tag1,tag2", posts.get(0).getTags());
    }

    @Test
    void testSave() {
        Post post = new Post(1L, "New Post", "new.jpg", "New Content", "tag1,tag2", 0, 0);
        String sql = "INSERT INTO posts(title, image_url, content, tags, likes_count, comments_count) VALUES(?, ?, ?, ?, ?, ?)";

        postRepository.save(post);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(post.getTitle()), eq(post.getImageUrl()), eq(post.getContent()), eq(post.getTags()), eq(0), eq(0));
    }

    @Test
    void testUpdateLikes() {
        Long postId = 1L;
        int likesCount = 100;
        String sql = "UPDATE posts SET likes_count = ? WHERE id = ?";

        postRepository.updateLikes(postId, likesCount);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(likesCount), eq(postId));
    }

    @Test
    void testUpdateComments() {
        Long postId = 1L;
        int commentsCount = 50;
        String sql = "UPDATE posts SET comments_count = ? WHERE id = ?";

        postRepository.updateComments(postId, commentsCount);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(commentsCount), eq(postId));
    }

    @Test
    void testUpdate() {
        Post post = new Post(1L, "Updated Title", "updated.jpg", "Updated Content", "tag1,tag3", 15, 10);
        String sql = "UPDATE posts SET title = ?, image_url = ?, content = ?, tags = ?, likes_count = ?, comments_count = ? WHERE id = ?";

        postRepository.update(post);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(post.getTitle()), eq(post.getImageUrl()), eq(post.getContent()), eq(post.getTags()), eq(post.getLikesCount()), eq(post.getCommentsCount()), eq(post.getId()));
    }

    @Test
    void testDeleteById() {
        Long postId = 1L;
        String sql = "DELETE FROM posts WHERE id = ?";

        postRepository.deleteById(postId);

        verify(jdbcTemplate, times(1)).update(eq(sql), eq(postId));
    }

    @Test
    void testFindById_Found() {
        Long postId = 1L;
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts WHERE id = ?";
        Post expectedPost = new Post(postId, "Title", "image.jpg", "Content", "tag1,tag2", 10, 5);

        when(jdbcTemplate.queryForObject(eq(sql), any(Object[].class), any(RowMapper.class)))
                .thenReturn(expectedPost);

        Post post = postRepository.findById(postId);

        assertNotNull(post);
        assertEquals("Title", post.getTitle());
    }

    @Test
    void testFindById_NotFound() {
        Long postId = 1L;
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), any(Object[].class), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Post post = postRepository.findById(postId);

        assertNull(post);
    }
}



