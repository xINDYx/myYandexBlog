package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private MockMvc mockMvc;
    private Post testPost;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        testPost = new Post(1L, "Test Post", "test-image.jpg", "Content of the test post", "test", 0, 0);
    }

    @Test
    void testGetPosts() throws Exception {
        when(postService.findAll(any(), anyInt(), anyInt())).thenReturn(List.of(testPost));

        mockMvc.perform(get("/posts")
                        .param("tag", "test")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("listposts"))
                .andExpect(model().attributeExists("posts", "currentPage", "size", "tag"));
    }

    @Test
    void testSavePost() throws Exception {
        doNothing().when(postService).save(any());

        mockMvc.perform(post("/posts")
                        .param("title", "New Post")
                        .param("content", "New content")
                        .param("tags", "new-tag")
                        .param("imageUrl", "new-image.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts"));

        verify(postService, times(1)).save(any());
    }

    @Test
    void testDeletePost() throws Exception {
        doNothing().when(postService).deleteById(1L);

        mockMvc.perform(post("/posts/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts"));

        verify(postService, times(1)).deleteById(1L);
    }

    @Test
    void testGetPostById() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);
        when(postService.getCommentsByPostId(1L)).thenReturn(List.of(new Comment(1L, 1L, "Test Comment")));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("listposts"))
                .andExpect(model().attributeExists("post", "comments"));
    }

    @Test
    void testGetPostById_NotFound() throws Exception {
        when(postService.getPostById(1L)).thenReturn(null);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    void testEditPost() throws Exception {
        doNothing().when(postService).updatePost(eq(1L), any());

        mockMvc.perform(post("/posts/1/edit")
                        .param("title", "Updated Post")
                        .param("content", "Updated content")
                        .param("tags", "updated-tag")
                        .param("imageUrl", "updated-image.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts/1"));

        verify(postService, times(1)).updatePost(eq(1L), any());
    }

    @Test
    void testLikePost() throws Exception {
        doNothing().when(postService).likePost(1L);

        mockMvc.perform(post("/posts/1/like"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts/1"));

        verify(postService, times(1)).likePost(1L);
    }

    @Test
    void testAddComment() throws Exception {
        doNothing().when(postService).addComment(1L, "New Comment");

        mockMvc.perform(post("/posts/1/comment")
                        .param("content", "New Comment")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listposts/1"));

        verify(postService, times(1)).addComment(1L, "New Comment");
    }
}
