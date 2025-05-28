package home.example.board.service.imageArch;

import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.domain.Image;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class ImageArchPreFetchServiceTest {

    @Autowired
    private ImageArchPreFetchService imageArchPreFetchService;

    // Example:
     @Test
     void testPreFetchImagePosts() {
         ImagePostRequestDTO requestDTO = new ImagePostRequestDTO();
         requestDTO.setLimit(10);
         List<Image> images = imageArchPreFetchService.preFetchImagePosts(requestDTO);
         for (Image image : images) {
                System.out.println("Image: " + image.getPost_id() + ", " + image.getImage_url());
         };
         assertNotNull(images);
         assertFalse(images.isEmpty());
     }
}