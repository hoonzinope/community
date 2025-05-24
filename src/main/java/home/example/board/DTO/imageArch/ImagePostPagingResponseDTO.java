package home.example.board.DTO.imageArch;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImagePostPagingResponseDTO {
    int limit;
    int offset;
    int totalCount;
    List<ImagePostDTO> imagePostList;
}
