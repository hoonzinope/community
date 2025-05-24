package home.example.board.repository.imageArch;

import home.example.board.domain.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImageMapper {
    List<Image> selectImageList(List<Long> postIds);
}
