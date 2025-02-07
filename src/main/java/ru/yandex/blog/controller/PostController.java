package ru.yandex.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String posts(@RequestParam(name = "tag", required = false) String tag,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        Model model) {
        int offset = (page - 1) * size;
        List<Post> posts = postService.findAll(tag, offset, size);

        posts.forEach(post -> {
            String shortContent = postService.getShortContent(post.getContent());
            post.setContent(shortContent);
        });

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("tag", tag);

        return "listposts";
    }

    @PostMapping
    public String save(@ModelAttribute Post post) {
        post.setLikesCount(0);
        post.setCommentsCount(0);
        postService.save(post);

        return "redirect:/listposts";
    }

    @PostMapping(value = "/{id}", params = "_method=delete")
    public String delete(@PathVariable(name = "id") Long id) {
        postService.deleteById(id);

        return "redirect:/listposts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/error";
        }
        List<Comment> comments = postService.getCommentsByPostId(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);

        return "listposts";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable("id") Long id, @ModelAttribute Post post) {
        postService.updatePost(id, post);
        return "redirect:/listposts/" + id;
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable("id") Long id) {
        postService.likePost(id);
        return "redirect:/listposts/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @RequestParam("content") String content) {
        postService.addComment(id, content);
        return "redirect:/listposts/" + id;
    }
}
