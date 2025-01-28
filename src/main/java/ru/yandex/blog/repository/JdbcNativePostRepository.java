package ru.yandex.blog.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.model.Post;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll(String tag, int offset, int limit) {
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts";
        List<Object> params = new ArrayList<>();

        if (tag != null && !tag.isBlank()) {
            sql += " WHERE tags LIKE ?";
            params.add("%" + tag + "%");
        }

        sql += " LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {


            return new Post(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("image_url"),
                    rs.getString("content"),
                    rs.getString("tags"),
                    rs.getInt("likes_count"),
                    rs.getInt("comments_count")
            );
        });
    }

    @Override
    public void save(Post post) {
        jdbcTemplate.update("INSERT INTO posts(title, image_url, content, tags, likes_count, comments_count) VALUES(?, ?, ?, ?, ?, ?)",
                post.getTitle(), post.getImageUrl(), post.getContent(), post.getTags(), 0, 0);
    }

    @Override
    public void updateLikes(Long id, int likesCount) {
        jdbcTemplate.update("UPDATE posts SET likes_count = ? WHERE id = ?", likesCount, id);
    }

    @Override
    public void updateComments(Long id, int commentsCount) {
        jdbcTemplate.update("UPDATE posts SET comments_count = ? WHERE id = ?", commentsCount, id);
    }

    @Override
    public void update(Post post) {
        jdbcTemplate.update("UPDATE posts SET title = ?, image_url = ?, content = ?, tags = ?, likes_count = ?, comments_count = ? WHERE id = ?",
                post.getTitle(), post.getImageUrl(), post.getContent(), post.getTags(), post.getLikesCount(), post.getCommentsCount(), post.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT id, title, image_url, content, tags, likes_count, comments_count FROM posts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("image_url"),
                            rs.getString("content"),
                            rs.getString("tags"),
                            rs.getInt("likes_count"),
                            rs.getInt("comments_count")
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
