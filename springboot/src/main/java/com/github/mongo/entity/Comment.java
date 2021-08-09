package com.github.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HAN
 * @version 1.0
 * @since 08-09-19:32
 */
@Document
@Data
@CompoundIndex(def = "{'user_id': -1, 'like_num': 1}")
public class Comment implements Serializable {

    @MongoId
    private String id;

    @Field("article_id")
    @Indexed
    private String articleId;

    @Field("content")
    private String content;

    @Field("user_id")
    private String userId;

    private String nickname;

    @Field("create_time")
    private LocalDateTime createTime;

    @Field("like_num")
    private Integer likeNum;

    private String state;


}
