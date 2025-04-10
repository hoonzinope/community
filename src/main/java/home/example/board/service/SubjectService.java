package home.example.board.service;

import home.example.board.dao.SubjectDAO;
import home.example.board.domain.Subject;
import home.example.board.repository.SubjectMapper;
import org.json.simple.JSONArray;
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

    public JSONArray getAllSubject() {
        JSONObject jsonObject = new JSONObject();
        List<Subject> subjectList = subjectDAO.getSubjectAll();
        Map<Long, JSONArray> subjectMap = new HashMap<>();
        for (Subject subject : subjectList) {
            long parentSeq = subject.getParent_subject_seq();
            if(parentSeq == 0) {
                continue; // Skip if parent_seq is 0
            }
            if (!subjectMap.containsKey(parentSeq)) {
                subjectMap.put(parentSeq, new JSONArray());
            }
            JSONObject subjectJson = new JSONObject();
            subjectJson.put("subject_seq", subject.getSubject_seq());
            subjectJson.put("subject_name", subject.getSubject_name());
            subjectMap.get(parentSeq).add(subjectJson);
        }
        JSONArray subjectArr = new JSONArray();
        for(Subject subject : subjectList) {
            long parentSeq = subject.getParent_subject_seq();
            long subjectSeq = subject.getSubject_seq();
            if(parentSeq == 0) {
                JSONObject subjectJson = new JSONObject();
                subjectJson.put("subject_seq", subject.getSubject_seq());
                subjectJson.put("subject_name", subject.getSubject_name());
                subjectJson.put("child_subject_list", subjectMap.get(subjectSeq));
                subjectArr.add(subjectJson);
            }
        }
        return subjectArr;
    }
}
