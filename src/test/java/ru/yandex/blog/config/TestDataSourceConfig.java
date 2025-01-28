package ru.yandex.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import ru.yandex.blog.controller.PostController;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.JdbcNativeCommentRepository;
import ru.yandex.blog.repository.JdbcNativePostRepository;
import ru.yandex.blog.repository.PostRepository;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import javax.sql.DataSource;

@Configuration
public class TestDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public CommentRepository commentRepository() {
        return new JdbcNativeCommentRepository(jdbcTemplate());
    }

    @Bean
    public PostRepository postRepository() {
        return new JdbcNativePostRepository(jdbcTemplate());
    }

    @Bean
    public CommentService commentService() {
        return new CommentService(postRepository(), commentRepository());
    }

    @Bean
    public PostService postService() {
        return new PostService(postRepository(), commentRepository());
    }

    @Bean
    public PostController postController() {
        return new PostController(postService());
    }
}
