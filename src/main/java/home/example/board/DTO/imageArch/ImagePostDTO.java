package home.example.board.DTO.imageArch;

import home.example.board.domain.Image;
import home.example.board.domain.ImagePost;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImagePostDTO {
    private ImagePost imagePost;
    private List<Image> images;
}
