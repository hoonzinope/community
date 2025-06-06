CREATE TABLE `subject` (
                           `subject_seq` bigint NOT NULL AUTO_INCREMENT,
                           `subject_name` varchar(100) NOT NULL,
                           `parent_subject_seq` bigint DEFAULT NULL,
                           `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                           `use_yn` char(1) NOT NULL DEFAULT 'Y' COMMENT 'Y: 사용, N: 미사용',
                           PRIMARY KEY (`subject_seq`),
                           KEY `fk_parent_subject` (`parent_subject_seq`),
                           CONSTRAINT `fk_parent_subject` FOREIGN KEY (`parent_subject_seq`) REFERENCES `subject` (`subject_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci