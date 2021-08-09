package com.github.mongo.dao;

import com.github.mongo.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author HAN
 * @version 1.0
 * @since 08-09-19:52
 */
public interface CommentRepository extends MongoRepository<Comment, String> {
}
