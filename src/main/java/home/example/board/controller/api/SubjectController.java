package home.example.board.controller.api;

import home.example.board.service.SubjectService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubjectController {

    @Autowired
    SubjectService subjectService;

    @GetMapping("/api/subjects")
    public ResponseEntity<JSONObject> getSubjects() {
        JSONObject subjectList = subjectService.selectSubjectList();
        return ResponseEntity.ok().body(subjectList);
    }
}
