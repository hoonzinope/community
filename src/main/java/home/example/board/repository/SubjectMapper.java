package home.example.board.repository;

import home.example.board.domain.Subject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectMapper {
    List<Subject> getSubjectListUseN();
    Subject getSubject(long subject_seq);
    List<Subject> getSiblingSubjectList(long subject_seq);
    List<Subject> getSubjectList();
    List<Subject> getParentsSubjectList();
    List<Subject> getChildSubjectList(long parent_seq);
}
