package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void testEditComment() throws Exception {
        Long commentId = 1L;
        String newContent = "Updated comment content";
        Long postId = 2L;

        Comment comment = new Comment(commentId, postId, "Old content");

        when(commentService.findById(commentId)).thenReturn(comment);
        when(commentService.getPostIdByCommentId(commentId)).thenReturn(postId);

        mockMvc.perform(post("/comments/{id}/edit", commentId)
                        .param("content", newContent))
                .andExpect(status().is3xxRedirection())  // проверка на редирект
                .andExpect(header().string("Location", "/posts/" + postId));

        verify(commentService, times(1)).updateComment(comment);
    }

    @Test
    void testDeleteComment() throws Exception {
        Long commentId = 1L;
        Long postId = 2L;

        when(commentService.getPostIdByCommentId(commentId)).thenReturn(postId);

        mockMvc.perform(post("/comments/{id}/delete", commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/posts/" + postId));

        verify(commentService, times(1)).deleteById(commentId);
        verify(postService, times(1)).decreaseCommentCount(postId);
    }
}
