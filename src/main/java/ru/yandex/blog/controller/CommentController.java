package ru.yandex.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/{id}/edit")
    public String editComment(@PathVariable("id") Long id, @RequestParam(name = "content") String content) {
        Comment comment = commentService.findById(id);
        comment.setContent(content);
        commentService.saveComment(comment);

        return "redirect:/listposts/" + commentService.getPostIdByCommentId(id);
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable("id") Long id) {
        Long postId = commentService.getPostIdByCommentId(id);
        commentService.deleteById(id);
        postService.decreaseCommentCount(postId);

        return "redirect:/listposts/" + postId;
    }
}
