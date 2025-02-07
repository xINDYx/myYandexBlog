package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.exception.CommentNotFoundException;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment findById(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException("Comment with id " + id + " not found");
        }
        return commentOptional.get();
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public Long getPostIdByCommentId(Long id) {
        return commentRepository.getPostIdByCommentId(id);
    }
}
