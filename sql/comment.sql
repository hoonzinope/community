CREATE TABLE `comment` (
   `comment_seq` int NOT NULL AUTO_INCREMENT,
   `post_seq` int NOT NULL,
   `content` text NOT NULL,
   `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `delete_flag` tinyint(1) DEFAULT '0',
   `delete_ts` timestamp NULL DEFAULT NULL,
   `parent_comment_seq` int DEFAULT NULL,
   `user_seq` int NOT NULL,
   PRIMARY KEY (`comment_seq`),
   KEY `post_seq` (`post_seq`),
   KEY `parent_comment_seq` (`parent_comment_seq`),
   KEY `user_seq` (`user_seq`),
   CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`post_seq`) REFERENCES `post` (`post_seq`) ON DELETE CASCADE,
   CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`parent_comment_seq`) REFERENCES `comment` (`comment_seq`) ON DELETE CASCADE,
   CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`user_seq`) REFERENCES `user` (`user_seq`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci