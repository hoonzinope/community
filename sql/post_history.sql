CREATE TABLE `post_history` (
    `post_hs_seq` int NOT NULL AUTO_INCREMENT,
    `post_seq` int NOT NULL,
    `title` varchar(255) NOT NULL,
    `content` text NOT NULL,
    `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`post_hs_seq`),
    KEY `post_seq` (`post_seq`),
    CONSTRAINT `post_history_ibfk_1` FOREIGN KEY (`post_seq`) REFERENCES `post` (`post_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci