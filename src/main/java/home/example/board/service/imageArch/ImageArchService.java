package home.example.board.service.imageArch;

import home.example.board.DTO.imageArch.ImagePostDTO;
import home.example.board.DTO.imageArch.ImagePostPagingResponseDTO;
import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.dao.imageArch.ImageDAO;
import home.example.board.dao.imageArch.ImagePostDAO;
import home.example.board.domain.Image;
import home.example.board.domain.ImagePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageArchService {

    private final ImagePostDAO imagePostDAO;
    private final ImageDAO imageDAO;

    @Autowired
    public ImageArchService(ImagePostDAO imagePostDAO, ImageDAO imageDAO) {
        this.imagePostDAO = imagePostDAO;
        this.imageDAO = imageDAO;
    }

    public ImagePostPagingResponseDTO getImagePostList(ImagePostRequestDTO imagePostRequestDTO) {
        // Fetch image posts with pagination
        List<ImagePost> imagePosts = selectImagePostList(imagePostRequestDTO);
        int imagePostTotalCount = selectImagePostCount();

        // Extract post IDs from the fetched image posts
        List<Long> postIds = imagePosts.stream()
                                       .map(ImagePost::getPost_id)
                                       .collect(Collectors.toList());
        Long lastId = imagePosts.isEmpty() ? null : imagePosts.get(imagePosts.size() - 1).getPost_id();
        // Fetch images associated with the post IDs
        List<Image> images = selectImageList(postIds);

        // You can now process or return the image posts and their associated images as needed
        List<ImagePostDTO> imagePostDTOS = imagePosts.stream().map(imagePost -> {
            List<Image> associatedImages = images.stream()
                    .filter(image -> image.getPost_id() == imagePost.getPost_id())
                    .collect(Collectors.toList());
            return ImagePostDTO.builder()
                    .imagePost(imagePost)
                    .images(associatedImages)
                    .build();
        }).collect(Collectors.toList());

        return ImagePostPagingResponseDTO.builder()
                .limit(imagePostRequestDTO.getLimit())
                .totalCount(imagePostTotalCount)
                .imagePostList(imagePostDTOS)
                .lastId(lastId)
                .build();
    }

    private List<ImagePost> selectImagePostList(ImagePostRequestDTO imagePostRequestDTO) {
        // Implement the logic to get image posts with pagination
        return imagePostDAO.getImagePostList(imagePostRequestDTO);
    }

    private int selectImagePostCount() {
        // Implement the logic to get the count of image posts
        return imagePostDAO.getImagePostCount();
    }

    private List<Image> selectImageList(List<Long> postIds) {
        List<Image> images = imageDAO.selectImageList(postIds);
        images.forEach(image -> {
            // Set the image URL to a base64 encoded string for the image ID
            String imageUrl = image.getImage_url();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageUrl = Base64.getEncoder().encodeToString(imageUrl.getBytes(StandardCharsets.UTF_8));
                image.setImage_url("/api/image-proxy/?url=" + imageUrl);
            }else{
                image.setImage_url(null); // Set to null if image_url is empty
            }

            String thumb_url = image.getThumb_url();
            if (thumb_url != null && !thumb_url.isEmpty()) {
                thumb_url = Base64.getEncoder().encodeToString(thumb_url.getBytes(StandardCharsets.UTF_8));
                image.setThumb_url("/api/image-proxy/?url=" + thumb_url);
            } else {
                image.setThumb_url(null); // Set to null if thumb_url is empty
            }
        });
        return images;
    }
}
