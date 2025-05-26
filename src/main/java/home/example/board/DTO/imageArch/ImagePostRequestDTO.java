package home.example.board.DTO.imageArch;

import lombok.Data;

@Data
public class ImagePostRequestDTO {
    private int offset;
    private int limit;
    private Long lastId;
}
