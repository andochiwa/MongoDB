package com.github.mongo.service;

import com.github.mongo.dao.CommentRepository;
import com.github.mongo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
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
}
