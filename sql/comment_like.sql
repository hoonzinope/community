CREATE TABLE `comment_like` (
        `comment_like_seq` int NOT NULL AUTO_INCREMENT,
        `user_seq` int NOT NULL,
        `comment_seq` int NOT NULL,
        `like_type` tinyint NOT NULL,
        `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`comment_like_seq`),
        UNIQUE KEY `user_seq` (`user_seq`, `comment_seq`),
        KEY `comment_seq` (`comment_seq`),
        CONSTRAINT `comment_like_ibfk_1` FOREIGN KEY (`user_seq`) REFERENCES `user` (`user_seq`) ON DELETE CASCADE,
        CONSTRAINT `comment_like_ibfk_2` FOREIGN KEY (`comment_seq`) REFERENCES `comment` (`comment_seq`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci