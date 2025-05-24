package home.example.board.dao.imageArch;

import home.example.board.domain.Image;
import home.example.board.repository.imageArch.ImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageDAO {
    private final ImageMapper imageMapper;
    @Autowired
    public ImageDAO(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public List<Image> selectImageList(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if postIds is null or empty
        }
        return imageMapper.selectImageList(postIds);
    }
}
