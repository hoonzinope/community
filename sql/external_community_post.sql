CREATE TABLE `external_community_post` (
                                           `post_id` bigint NOT NULL AUTO_INCREMENT,
                                           `site_code` varchar(40) NOT NULL,
                                           `post_title` varchar(300) NOT NULL,
                                           `post_url` varchar(600) NOT NULL,
                                           `author` varchar(120) DEFAULT NULL,
                                           `post_created_at` datetime DEFAULT NULL,
                                           `fetched_at` datetime NOT NULL,
                                           `nsfw` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'NSFW 여부 (0: 일반, 1: NSFW)',
                                           PRIMARY KEY (`post_id`),
                                           UNIQUE KEY `uk_post_url` (`post_url`),
                                           KEY `idx_site_url` (`site_code`,`post_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci