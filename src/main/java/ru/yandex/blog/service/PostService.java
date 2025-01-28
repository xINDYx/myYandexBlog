package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> findAll(String tag, int offset, int limit) {
        return postRepository.findAll(tag, offset, limit);
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void updatePost(Long id, Post post) {
        Post existing = getPostById(id);
        existing.setTitle(post.getTitle());
        existing.setImageUrl(post.getImageUrl());
        existing.setContent(post.getContent());
        existing.setTags(post.getTags());
        postRepository.update(existing);
    }

    public void likePost(Long id) {
        Post post = getPostById(id);
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.updateLikes(id, post.getLikesCount());
    }

    public void increaseCommentCount(Long id) {
        Post post = getPostById(id);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.updateComments(id, post.getCommentsCount());
    }

    public void decreaseCommentCount(Long id) {
        Post post = getPostById(id);
        post.setCommentsCount(post.getCommentsCount() - 1);
        postRepository.updateComments(id, post.getCommentsCount());
    }

    public void addComment(Long postId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(content);
        commentRepository.save(comment);

        increaseCommentCount(postId);
    }

    public String getShortContent(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String[] paragraphs = content.split("\n\\s*\n");

        String firstParagraph = paragraphs[0];

        String[] lines = firstParagraph.split("\n");
        return Arrays.stream(lines)
                .limit(3)
                .collect(Collectors.joining("\n"));
    }
}
