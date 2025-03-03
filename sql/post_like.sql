CREATE TABLE `post_like` (
     `post_like_seq` int NOT NULL AUTO_INCREMENT,
     `user_seq` int NOT NULL,
     `post_seq` int NOT NULL,
     `like_type` varchar(7) NOT NULL,
     `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`post_like_seq`),
     UNIQUE KEY `user_seq` (`user_seq`,`post_seq`),
     KEY `post_seq` (`post_seq`),
     CONSTRAINT `post_like_ibfk_1` FOREIGN KEY (`user_seq`) REFERENCES `user` (`user_seq`) ON DELETE CASCADE,
     CONSTRAINT `post_like_ibfk_2` FOREIGN KEY (`post_seq`) REFERENCES `post` (`post_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci