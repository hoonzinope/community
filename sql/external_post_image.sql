CREATE TABLE `external_post_image` (
                                       `image_id` bigint NOT NULL AUTO_INCREMENT,
                                       `post_id` bigint NOT NULL,
                                       `image_url` varchar(600) NOT NULL,
                                       `image_index` int NOT NULL DEFAULT '0',
                                       `thumb_url` varchar(600) DEFAULT NULL,
                                       `status` varchar(40) DEFAULT 'normal',
                                       `fetched_at` datetime NOT NULL,
                                       PRIMARY KEY (`image_id`),
                                       UNIQUE KEY `uniq_post_image_url` (`post_id`,`image_url`),
                                       KEY `idx_post_image` (`post_id`,`image_index`),
                                       CONSTRAINT `external_post_image_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `external_community_post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci