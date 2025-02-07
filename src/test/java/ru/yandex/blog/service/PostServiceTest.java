package ru.yandex.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.blog.exception.PostNotFoundException;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setImageUrl("http://example.com/image.jpg");
        post.setTags("tag1, tag2");
        post.setLikesCount(10);
        post.setCommentsCount(5);

        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setContent("Test comment");

        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, commentRepository);
    }

    @Test
    void testFindAll() {
        when(postRepository.findAll(anyString(), anyInt(), anyInt())).thenReturn(Arrays.asList(post));

        var posts = postService.findAll("tag1", 0, 10);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Test Post", posts.get(0).getTitle());
        verify(postRepository, times(1)).findAll(anyString(), anyInt(), anyInt());
    }

    @Test
    void testGetPostById_PostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
        assertEquals("Post with id 1 not found", exception.getMessage());
    }

    @Test
    void testGetPostById_Success() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals("Test Post", result.getTitle());
    }

    @Test
    void testUpdatePost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post updatedPost = new Post();
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        postService.updatePost(1L, updatedPost);

        assertEquals("Updated Title", post.getTitle());
        assertEquals("Updated Content", post.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testLikePost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        doNothing().when(postRepository).updateLikes(anyLong(), anyInt());

        postService.likePost(1L);

        assertEquals(11, post.getLikesCount());
        verify(postRepository, times(1)).updateLikes(anyLong(), anyInt());
    }

    @Test
    void testIncreaseCommentCount() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        doNothing().when(postRepository).updateComments(anyLong(), anyInt());


        postService.increaseCommentCount(1L);

        assertEquals(6, post.getCommentsCount());
        verify(postRepository, times(1)).updateComments(anyLong(), anyInt());
    }

    @Test
    void testDecreaseCommentCount() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        doNothing().when(postRepository).updateComments(anyLong(), anyInt());
        postService.decreaseCommentCount(1L);

        assertEquals(4, post.getCommentsCount());
        verify(postRepository, times(1)).updateComments(anyLong(), anyInt());
    }

    @Test
    void testAddComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        postService.addComment(1L, "New comment");

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(postRepository, times(1)).updateComments(anyLong(), anyInt());
        assertEquals(6, post.getCommentsCount());
    }

    @Test
    void testGetShortContent() {
        String shortContent = postService.getShortContent("This is the first paragraph.\n\nThis is the second paragraph.");
        assertEquals("This is the first paragraph.", shortContent);

        String emptyContent = postService.getShortContent("");
        assertEquals("", emptyContent);
    }
}
