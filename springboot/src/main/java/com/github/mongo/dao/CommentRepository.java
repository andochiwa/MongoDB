package com.github.mongo.dao;

import com.github.mongo.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author HAN
 * @version 1.0
 * @since 08-09-19:52
 */
public interface CommentRepository extends MongoRepository<Comment, String> {

    // 分页查询，命名格式必须严格按照要求
    Page<Comment> findByArticleId(String articleId, Pageable pageable);
}
