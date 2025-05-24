package home.example.board.domain;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "external_post_image")
public class Image {
    private long image_id;
    private long post_id;
    private String image_url;
    private int image_index;
    private String thumb_url;
    private String status;
    private LocalDateTime fetched_at;
}
