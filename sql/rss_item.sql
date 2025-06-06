CREATE TABLE `rss_items` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `title` varchar(500) NOT NULL,
                             `link` varchar(512) NOT NULL,
                             `description` varchar(2000) DEFAULT NULL COMMENT 'description',
                             `pub_date` datetime DEFAULT NULL,
                             `source` varchar(100) DEFAULT NULL,
                             `collected` tinyint(1) DEFAULT '0',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `link` (`link`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci