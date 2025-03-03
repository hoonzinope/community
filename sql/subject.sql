CREATE TABLE `subject` (
       `subject_seq` int NOT NULL AUTO_INCREMENT,
       `subject_name` varchar(100) NOT NULL,
       `insert_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
       PRIMARY KEY (`subject_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci