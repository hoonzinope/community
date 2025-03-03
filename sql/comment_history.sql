create table `comment_history`
(
    `comment_hs_seq` int NOT NULL AUTO_INCREMENT,
    `comment_seq` int NOT NULL,
    `content` text NOT NULL,
    `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`comment_hs_seq`),
    CONSTRAINT `comment_history_ibfk_1` FOREIGN KEY (`comment_seq`) REFERENCES `comment` (`comment_seq`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci