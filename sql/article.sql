CREATE TABLE `articles` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `rss_item_id` bigint DEFAULT NULL,
                            `url` varchar(512) NOT NULL,
                            `og_title` varchar(500) DEFAULT NULL,
                            `og_description` text,
                            `og_image` varchar(1000) DEFAULT NULL,
                            `content` mediumtext,
                            `posted` tinyint(1) DEFAULT '0',
                            `posted_ts` datetime DEFAULT NULL,
                            `parse_failed` tinyint(1) DEFAULT '0',
                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `rss_item_id` (`rss_item_id`),
                            CONSTRAINT `articles_ibfk_1` FOREIGN KEY (`rss_item_id`) REFERENCES `rss_items` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci