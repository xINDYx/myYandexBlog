package ru.yandex.blog.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.blog.model.Post;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void testSaveAndFindAll() {
        Post post1 = new Post(null, "Title1", "img1.jpg", "Content1", "java,spring", 10, 5);
        Post post2 = new Post(null, "Title2", "img2.jpg", "Content2", "kotlin,java", 15, 8);
        postRepository.saveAll(List.of(post1, post2));

        List<Post> allPosts = postRepository.findAll(null, 0, 10);
        assertThat(allPosts).hasSize(2);

        List<Post> javaPosts = postRepository.findAll("java", 0, 10);
        assertThat(javaPosts).hasSize(2);

        List<Post> kotlinPosts = postRepository.findAll("kotlin", 0, 10);
        assertThat(kotlinPosts).hasSize(1);
    }

    @Test
    void testUpdateLikes() {
        Post post = new Post(null, "Title", "img.jpg", "Content", "java", 5, 2);
        post = postRepository.save(post);

        postRepository.updateLikes(post.getId(), 20);
        Optional<Post> updatedPost = postRepository.findById(post.getId());

        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getLikesCount()).isEqualTo(20);
    }

    @Test
    void testUpdateComments() {
        Post post = new Post(null, "Title", "img.jpg", "Content", "java", 5, 2);
        post = postRepository.save(post);

        postRepository.updateComments(post.getId(), 10);
        Optional<Post> updatedPost = postRepository.findById(post.getId());

        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getCommentsCount()).isEqualTo(10);
    }

    @Test
    void testDeleteById() {
        Post post = new Post(null, "Title", "img.jpg", "Content", "java", 5, 2);
        post = postRepository.save(post);

        postRepository.deleteById(post.getId());

        Optional<Post> deletedPost = postRepository.findById(post.getId());
        assertThat(deletedPost).isEmpty();
    }
}



