package home.example.board.repository.imageArch;

import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.domain.ImagePost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImagePostMapper {
    List<ImagePost> selectImagePostList(ImagePostRequestDTO imagePostRequestDTO);
    int selectImagePostCount();
}
