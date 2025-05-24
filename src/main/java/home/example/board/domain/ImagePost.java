package home.example.board.domain;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "external_community_post")
public class ImagePost {
    private long post_id;
    private String site_code;
    private String post_title;
    private String post_url;
    private String author;
    private LocalDateTime post_created_at;
    private String fetched_at;
}
