package home.example.board.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Outbox {
    Long id;
    String aggregate_type;
    Long aggregate_id;
    String event_type;
    String payload;
    LocalDateTime created_ts;
    LocalDateTime processed_ts;
    String status; // PENDING, COMPLETED, FAILED
}
