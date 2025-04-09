package home.example.board.service;

import home.example.board.dao.SubjectDAO;
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
    SubjectDAO subjectDAO;

    public JSONObject selectSiblingSubject(long subject_seq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectDAO.getSiblingSubject(subject_seq));
        return jsonObject;
    }

    public JSONObject getSubTopicAndMainTopic(long subject_seq) {
        return subjectDAO.getSubjectNameAndParentSubjectName(subject_seq);
    }


    public JSONObject getMajorSubjectList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectDAO.getParentsSubjectList());
        return jsonObject;
    }

    public JSONObject getMinorSubjectList(long parent_seq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subjectList", subjectDAO.getChildSubjectList(parent_seq));
        return jsonObject;
    }
}
