package ru.yandex.blog.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;
import ru.yandex.blog.exception.CommentNotFoundException;

import java.util.Optional;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentService(postRepository, commentRepository);
    }

    @Test
    void testFindById_Success() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment foundComment = commentService.findById(1L);

        assertNotNull(foundComment);
        assertEquals(1L, foundComment.getId());
        assertEquals("Test Comment", foundComment.getContent());
    }

    @Test
    void testFindById_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.findById(1L);
        });
        assertEquals("Comment with id 1 not found", exception.getMessage());
    }

    @Test
    void testSaveComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("New Comment");

        commentService.saveComment(comment);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testDeleteById() {
        Long commentId = 1L;

        commentService.deleteById(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testGetPostIdByCommentId() {
        Long commentId = 1L;
        Long postId = 2L;

        when(commentRepository.getPostIdByCommentId(commentId)).thenReturn(postId);

        Long retrievedPostId = commentService.getPostIdByCommentId(commentId);

        assertEquals(postId, retrievedPostId);
    }
}
