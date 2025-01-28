package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment findById(Long id){
        return commentRepository.findById(id);
    }

    public void updateComment(Comment comment) {
        commentRepository.update(comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public Long getPostIdByCommentId(Long id) {
        return commentRepository.getPostIdByCommentId(id);
    }
}
