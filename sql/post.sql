CREATE TABLE `post` (
    `post_seq` int NOT NULL AUTO_INCREMENT,
    `title` varchar(255) NOT NULL,
    `content` text NOT NULL,
    `view_count` int DEFAULT '0',
    `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `delete_flag` tinyint(1) DEFAULT '0',
    `delete_ts` timestamp NULL DEFAULT NULL,
    `subject_seq` int NOT NULL,
    `user_seq` int NOT NULL,
    PRIMARY KEY (`post_seq`),
    KEY `subject_seq` (`subject_seq`),
    KEY `user_seq` (`user_seq`),
    CONSTRAINT `post_ibfk_1` FOREIGN KEY (`subject_seq`) REFERENCES `subject` (`subject_seq`) ON DELETE CASCADE,
    CONSTRAINT `post_ibfk_2` FOREIGN KEY (`user_seq`) REFERENCES `user` (`user_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci