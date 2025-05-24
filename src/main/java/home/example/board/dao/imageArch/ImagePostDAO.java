package home.example.board.dao.imageArch;

import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.domain.ImagePost;
import home.example.board.repository.imageArch.ImagePostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagePostDAO {
    private final ImagePostMapper imagePostMapper;

    @Autowired
    public ImagePostDAO(ImagePostMapper imagePostMapper) {
        this.imagePostMapper = imagePostMapper;
    }

    public List<ImagePost> getImagePostList(ImagePostRequestDTO imagePostRequestDTO) {
        return imagePostMapper.selectImagePostList(imagePostRequestDTO);
    }

    public int getImagePostCount() {
        return imagePostMapper.selectImagePostCount();
    }
}
