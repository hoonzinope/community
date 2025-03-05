package home.example.board.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Subject {
    private long subject_seq;
    private String subject_name;
    private LocalDateTime insert_ts;
}
