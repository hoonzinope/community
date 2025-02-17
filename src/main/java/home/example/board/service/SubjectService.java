package home.example.board.service;

import home.example.board.repository.SubjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubjectService {

    @Autowired
    SubjectMapper subjectMapper;

    public JSONObject selectSubjectList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectMapper.getSubjectList());
        return jsonObject;
    }

    public JSONObject selectSubject(long subject_seq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subject", subjectMapper.getSubject(subject_seq));
        return jsonObject;
    }

    public Map<Integer, String> getSubjectMap() {
        Map<Integer, String> subjectMap = new HashMap<>();
        subjectMapper.getSubjectList().forEach(subject -> {
            subjectMap.put(subject.getSubject_seq(), subject.getSubject_name());
        });
        return subjectMap;
    }
}
