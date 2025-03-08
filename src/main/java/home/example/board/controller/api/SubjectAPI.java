package home.example.board.controller.api;

import home.example.board.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubjectAPI {

    @Autowired
    SubjectService subjectService;

    @Operation(summary = "게시판 메뉴 목록 조회", description = "게시판 메뉴 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "게시판 메뉴 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subjectList\" : [{\"subject_id\" : 1, \"subject_name\" : \"자유게시판\"}, {\"subject_id\" : 2, \"subject_name\" : \"질문게시판\"}]}")
                                    }
                            )
            })
    })
    @GetMapping("/api/subjects")
    public ResponseEntity<JSONObject> getSubjects() {
        JSONObject subjectList = subjectService.selectSubjectList();
        return ResponseEntity.ok().body(subjectList);
    }
}
