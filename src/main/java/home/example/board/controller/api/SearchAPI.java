package home.example.board.controller.api;

import home.example.board.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class SearchAPI {

    @Autowired
    private SearchService searchService;

    @Operation(summary = "검색 API", description = "검색어를 입력받아 검색 결과를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/search")
    public ResponseEntity<JSONObject> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 요청 데이터",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = Map.class),
                            examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name="SearchRequest",
                                            value ="{ \"keyword\": \"검색어\", \"offset\": 0, \"limit\": 10, \"type\": \"all\"}"
                                        )
                                    }
                    )
            )
            @RequestBody Map<String, Object> requestMap) {
        log.info("SearchAPI - requestMap = {}", requestMap);
        String keyword = (String) requestMap.get("keyword");
        int offset = (int) requestMap.get("offset");
        int limit = (int) requestMap.get("limit");
        String type = (String) requestMap.get("type");
        // Long subject_seq =  Long.parseLong(requestMap.get("subject_seq").toString());

        JSONObject result = new JSONObject();
        result.put("keyword", keyword);
        result.put("offset", offset);
        result.put("limit", limit);
        result.put("type", type);
        //result.put("subject_seq", subject_seq);
        try{
            result = searchService.search(keyword, offset, limit, type);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("SearchAPI - search error: {}", e.getMessage());
            result.put("error", "검색 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(result);
        }
    }
}
