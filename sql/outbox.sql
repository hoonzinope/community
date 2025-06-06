CREATE TABLE `outbox` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `aggregate_type` varchar(50) NOT NULL COMMENT '대상 엔티티 유형 (예: post, comment)',
                          `aggregate_id` bigint NOT NULL COMMENT '대상 엔티티의 식별자 (post_seq 또는 comment_seq)',
                          `event_type` varchar(50) NOT NULL COMMENT '이벤트 유형 (예: CREATED, UPDATED, DELETED)',
                          `payload` json NOT NULL COMMENT '이벤트 관련 데이터를 JSON 형식으로 저장',
                          `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '이벤트 생성 시각',
                          `processed_ts` timestamp NULL DEFAULT NULL COMMENT '이벤트 전파가 완료된 시각',
                          `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '전파 상태 (예: PENDING, COMPLETED, FAILED)',
                          PRIMARY KEY (`id`),
                          KEY `idx_created_ts` (`created_ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci