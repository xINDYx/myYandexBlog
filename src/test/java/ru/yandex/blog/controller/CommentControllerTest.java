package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void testEditComment() throws Exception {
        Long commentId = 1L;
        String newContent = "Updated comment content";
        Long postId = 2L;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent(newContent);

        when(commentService.findById(commentId)).thenReturn(comment);
        when(commentService.getPostIdByCommentId(commentId)).thenReturn(postId);
        doNothing().when(commentService).saveComment(any(Comment.class));

        mockMvc.perform(post("/comments/{id}/edit", commentId)
                        .param("content", newContent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts/" + postId));

        verify(commentService, times(1)).saveComment(comment);
    }

    @Test
    void testDeleteComment() throws Exception {
        Long commentId = 1L;
        Long postId = 2L;

        when(commentService.getPostIdByCommentId(commentId)).thenReturn(postId);
        doNothing().when(commentService).deleteById(commentId);
        doNothing().when(postService).decreaseCommentCount(postId);

        mockMvc.perform(post("/comments/{id}/delete", commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts/" + postId));

        verify(commentService, times(1)).deleteById(commentId);
        verify(postService, times(1)).decreaseCommentCount(postId);
    }
}
