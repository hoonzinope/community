CREATE TABLE `bot_task` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `event_type` varchar(32) NOT NULL,
                            `payload` json NOT NULL,
                            `status` varchar(16) NOT NULL DEFAULT 'READY',
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `retry_count` int NOT NULL DEFAULT '0',
                            `last_error` text,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci