package home.example.board.controller.api;

import home.example.board.domain.Post;
import home.example.board.service.PostService;
import home.example.board.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchAPI {

    @Autowired
    private SearchService searchService;

    @Autowired
    private PostService postService;

    @Operation(summary = "검색 API", description = "검색어를 입력받아 검색 결과를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/search")
    public ResponseEntity<JSONObject> search(
            @Parameter(description = "검색어", required = true)
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "offset", required = true)
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @Parameter(description = "limit", required = true)
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @Parameter(description = "type", required = true)
            @RequestParam(value = "type", defaultValue = "all") String type
    ) {

        JSONObject result = new JSONObject();
        try{
            result = searchService.search(keyword, offset, limit, type);
            if(result != null && result.containsKey("post_seq_list")){
                // 게시물 목록을 가져오기 위해 post_seq_list를 사용하여 게시물 조회
                List<Long> postSeqList = (List<Long>) result.get("post_seq_list");
                JSONObject postList = postService.getPostList(postSeqList);
                result.put("post_list", postList);
            }
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "검색 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(result);
        }
    }
}
