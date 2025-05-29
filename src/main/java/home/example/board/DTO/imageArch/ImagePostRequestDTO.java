package home.example.board.DTO.imageArch;

import lombok.Data;

@Data
public class ImagePostRequestDTO {
    private int limit;
    private Long lastId;
    private int nsfw; // 0: sfw, 1: nsfw
}
