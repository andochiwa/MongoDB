package com.github.mongo.service;

import com.github.mongo.entity.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HAN
 * @version 1.0
 * @since 08-09-20:00
 */
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void saveOrUpdate() {
        Comment comment = new Comment();
        comment.setContent("hello");
        comment.setUserId("1005");
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeNum(5);
        comment.setNickname("Jani");
        comment.setArticleId("1008");
        commentService.saveOrUpdate(comment);
    }

    @Test
    void deleteById() {
        String id = "61110ba6e8fe796ef426d078";

        commentService.deleteById(id);
    }

    @Test
    void getList() {
        List<Comment> comments = commentService.getList();
        comments.forEach(System.out::println);
    }

    @Test
    void getById() {
        String id = "61110ba6e8fe796ef426d078";

        Comment comment = commentService.getById(id);
        System.out.println(comment);
    }

    @Test
    void getListByArticleIdPage() {
        String articleId = "100001";
        int page = 1, size = 2;
        Page<Comment> commentPage = commentService.getListByArticleIdPage(articleId, page, size);
        System.out.println(commentPage.getTotalElements());
        commentPage.getContent().forEach(System.out::println);
    }

    @Test
    void updateLikeNum() {
        String id = "610d11cb775bec0fccfa990a";

        commentService.updateLikeNum(id);
    }
}
