CREATE TABLE `user` (
    `user_seq` int NOT NULL AUTO_INCREMENT,
    `user_name` varchar(50) NOT NULL,
    `user_pw` varchar(255) NOT NULL,
    `user_email` varchar(100) NOT NULL,
    `user_nickname` varchar(50) NOT NULL,
    `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `role` varchar(10) DEFAULT 'USER' COMMENT 'user role',
    PRIMARY KEY (`user_seq`),
    UNIQUE KEY `user_email` (`user_email`),
    UNIQUE KEY `user_nickname` (`user_nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci