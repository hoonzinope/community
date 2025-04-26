package home.example.board.controller.api;

import home.example.board.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                    value = "{\"subjectList\" : [{\"subject_seq\" : 1, \"subject_name\" : \"자유게시판\"}, {\"subject_seq\" : 2, \"subject_name\" : \"질문게시판\"}]}")
                                    }
                            )
            })
    })
    @GetMapping("/api/subjects/{subject_seq}")
    public ResponseEntity<JSONObject> getSubjects(
            @Parameter(description = "게시판 메뉴 ID", required = true)
            @PathVariable Long subject_seq
    )
    {
        JSONObject subjectList = subjectService.selectSiblingSubject(subject_seq);
        return ResponseEntity.ok().body(subjectList);
    }

    @Operation(summary = "게시판 부모 메뉴 목록 조회", description = "게시판 부모 메뉴 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "게시판 부모 메뉴 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subjectList\" : [{\"subject_seq\" : 1, \"subject_name\" : \"자유게시판\"}, {\"subject_seq\" : 2, \"subject_name\" : \"질문게시판\"}]}")
                                    }
                            )
            })
    })
    @GetMapping("/api/subject/majorSubjects")
    public ResponseEntity<JSONObject> getMajorSubjects() {
        JSONObject subjectList = subjectService.getMajorSubjectList();
        return ResponseEntity.ok().body(subjectList);
    }

    @Operation(summary = "게시판 자식 메뉴 목록 조회", description = "게시판 자식 메뉴 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "게시판 자식 메뉴 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subjectList\" : [{\"subject_seq\" : 1, \"subject_name\" : \"자유게시판\"}, {\"subject_seq\" : 2, \"subject_name\" : \"질문게시판\"}]}")
                                    }
                            )
            })
    })
    @PostMapping("/api/subject/minorSubjects")
    public ResponseEntity<JSONObject> getMinorSubjects(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "부모 메뉴 ID",
                    required = true,
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"major_seq\" : 1}")
                                    }
                            )
                    }
            )
            @RequestBody JSONObject subjectId
    ) {
        // Extract the subject_seq from the request body
        long subject_seq = Long.parseLong(subjectId.get("major_seq").toString());
        JSONObject subjectList = subjectService.getMinorSubjectList(subject_seq);
        return ResponseEntity.ok().body(subjectList);
    }

    @Operation(summary = "게시판 메뉴 ID로 이름 조회", description = "게시판 메뉴 ID로 이름을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "게시판 메뉴 ID로 이름 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subject_seq\" : 1, \"subject_name\" : \"자유게시판\", \"parent_subject_seq\" : null, \"parent_subject_name\" : null}")
                                    }
                            )
            })
    })
    @PostMapping("/api/subject/name")
    public ResponseEntity<JSONObject> getSubjectName(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시판 메뉴 ID",
                    required = true,
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subject_seq\" : 1}")
                                    }
                            )
                    }
            )
            @RequestBody JSONObject subjectId
    ) {
        // Extract the subject_seq from the request body
        long subject_seq = Long.parseLong(subjectId.get("subject_seq").toString());
        JSONObject subjectName = subjectService.getSubTopicAndMainTopic(subject_seq);
        return ResponseEntity.ok().body(subjectName);
    }

    @Operation(summary = "모든 게시판 메뉴 목록 조회", description = "모든 게시판 메뉴 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="200",
                    description = "모든 게시판 메뉴 목록 조회 성공",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "정상 response",
                                                    value = "{\"subjectList\" : [{\"subject_seq\" : 1, \"subject_name\" : \"자유게시판\"}, {\"subject_seq\" : 2, \"subject_name\" : \"질문게시판\"}]}")
                                    }
                            )
            })
    })
    @GetMapping("/api/subjects/all")
    public ResponseEntity<JSONObject> getAllSubject() {
        JSONObject subjectList = new JSONObject();
        subjectList.put("subjectList", subjectService.getAllSubject());
        return ResponseEntity.ok().body(subjectList);
    }
}
