package ru.yandex.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.model.Comment;

import java.util.List;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Comment comment) {
        jdbcTemplate.update(
                "INSERT INTO comments (post_id, content) VALUES (?, ?)",
                comment.getPostId(), comment.getContent()
        );
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT id, post_id, content FROM comments WHERE post_id = ?";

        return jdbcTemplate.query(sql, new Object[]{postId}, (rs, rowNum) ->
                new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("content")
                )
        );
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT id, post_id, content FROM comments WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("content")
                )
        );
    }


    public void update(Comment comment) {
        jdbcTemplate.update(
                "UPDATE comments SET post_id = ?, content = ? WHERE id = ?",
                comment.getPostId(), comment.getContent(), comment.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from comments where id = ?", id);
    }

    @Override
    public Long getPostIdByCommentId(Long id) {
        String sql = "SELECT post_id FROM comments WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, Long.class);
    }

}
