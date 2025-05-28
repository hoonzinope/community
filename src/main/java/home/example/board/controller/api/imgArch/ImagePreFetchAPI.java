package home.example.board.controller.api.imgArch;

import home.example.board.DTO.imageArch.ImagePostPagingResponseDTO;
import home.example.board.DTO.imageArch.ImagePostRequestDTO;
import home.example.board.service.imageArch.ImageArchPreFetchService;
import home.example.board.service.imageArch.ImageArchService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ImagePreFetchAPI {

    private final ImageArchPreFetchService imageArchPreFetchService;
    @Autowired
    public ImagePreFetchAPI(ImageArchPreFetchService imageArchPreFetchService) {
        this.imageArchPreFetchService = imageArchPreFetchService;
    }

    @PostMapping("/api/image-prefetch")
    public ResponseEntity<JSONObject> getImagePreFetch(
            @RequestBody ImagePostRequestDTO imagePostRequestDTO) {
        // Implement the logic to get image posts with pagination (for prefetching)
        log.info("getImage**PreFetch** called with request: {}", imagePostRequestDTO);
        JSONObject response = new JSONObject();
        try{
            imageArchPreFetchService.preFetchImagePosts(imagePostRequestDTO);
            response.put("status", "success");
            response.put("message", "Image posts prefetching completed successfully.");
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error occurred while prefetching image posts: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Error occurred while prefetching image posts: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
