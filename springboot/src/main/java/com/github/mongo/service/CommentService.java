package com.github.mongo.service;

import com.github.mongo.dao.CommentRepository;
import com.github.mongo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HAN
 * @version 1.0
 * @since 08-09-19:52
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveOrUpdate(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getList() {
        return commentRepository.findAll();
    }

    public Comment getById(String id) {
        return commentRepository.findById(id).orElse(null);
    }

    public Page<Comment> getListByArticleIdPage(String articleId, int page, int size) {
        return commentRepository.findByArticleId(articleId, PageRequest.of(page - 1, size));
    }

    // 点赞数+1
    public void updateLikeNum(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.inc("like_num");

        mongoTemplate.updateFirst(query, update, Comment.class);

    }
}
