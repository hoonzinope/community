package home.example.board.service;

import home.example.board.domain.Subject;
import home.example.board.repository.SubjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    SubjectMapper subjectMapper;

    public JSONObject selectSubjectList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectMapper.getSubjectList());
        return jsonObject;
    }

    public Map<Long, String> getSubjectMap() {
        Map<Long, String> subjectMap = new HashMap<>();
        subjectMapper.getSubjectList().forEach(subject -> {
            subjectMap.put(subject.getSubject_seq(), subject.getSubject_name());
        });
        return subjectMap;
    }

    public JSONObject getSubjectName(long subject_seq) {
        JSONObject jsonObject = new JSONObject();
        List<Subject> subjectList = subjectMapper.getSubjectList();
        Optional<Subject> optionalSubject = subjectList.stream()
                .filter(subject -> subject.getSubject_seq() == subject_seq)
                .findFirst();
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            long parentSubjectSeq = subject.getParent_subject_seq();
            if(parentSubjectSeq != 0) {
                Subject parentSubject = subjectList.stream()
                        .filter(s -> s.getSubject_seq() == parentSubjectSeq)
                        .findFirst()
                        .orElse(null);
                jsonObject.put("parent_subject_seq", parentSubject != null ? parentSubject.getSubject_seq() : null);
                jsonObject.put("parent_subject_name", parentSubject != null ? parentSubject.getSubject_name() : null);
            } else {
                jsonObject.put("parent_subject_seq", null);
                jsonObject.put("parent_subject_name", null);
            }
            jsonObject.put("subject_seq", subject.getSubject_seq());
            jsonObject.put("subject_name", subject.getSubject_name());
        } else {
            jsonObject.put("subject_seq", null);
            jsonObject.put("subject_name", null);
        }
        return jsonObject;
    }


    public JSONObject getParentSubjectList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectMapper.getParentsSubjectList());
        return jsonObject;
    }

    public JSONObject getChildSubjectList(long parent_seq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectMapper.getChildSubjectList(parent_seq));
        return jsonObject;
    }
}
