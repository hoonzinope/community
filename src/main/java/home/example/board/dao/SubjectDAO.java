package home.example.board.dao;

import home.example.board.domain.Subject;
import home.example.board.repository.SubjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SubjectDAO {

    @Autowired
    private SubjectMapper subjectMapper;

    public List<Subject> getSiblingSubject(long subject_seq) {
        return subjectMapper.getSiblingSubjectList(subject_seq);
    }

    public JSONObject getSubjectNameAndParentSubjectName(long subject_seq) {
        JSONObject jsonObject = new JSONObject();

        Subject subject = subjectMapper.getSubject(subject_seq);
        if (subject != null) {
            jsonObject.put("subject_seq", subject.getSubject_seq());
            jsonObject.put("subject_name", subject.getSubject_name());

            long parentSubjectSeq = subject.getParent_subject_seq();
            if (parentSubjectSeq != 0) {
                Subject parentSubject = subjectMapper.getSubject(parentSubjectSeq);
                jsonObject.put("parent_subject_seq", parentSubject != null ? parentSubject.getSubject_seq() : null);
                jsonObject.put("parent_subject_name", parentSubject != null ? parentSubject.getSubject_name() : null);
            } else {
                jsonObject.put("parent_subject_seq", null);
                jsonObject.put("parent_subject_name", null);
            }
        } else {
            jsonObject.put("subject_seq", null);
            jsonObject.put("subject_name", null);
        }

        return jsonObject;
    }

    public List<Subject> getParentsSubjectList() {
        return subjectMapper.getParentsSubjectList();
    }

    public List<Subject> getChildSubjectList(long parent_seq) {
        return subjectMapper.getChildSubjectList(parent_seq);
    }

    public List<Subject> getSubjectAll() {
        return subjectMapper.getSubjectList();
    }

    public List<Subject> getSubjectListUseN() {
        return subjectMapper.getSubjectListUseN();
    }
}
