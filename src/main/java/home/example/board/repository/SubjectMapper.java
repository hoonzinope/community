package home.example.board.repository;

import home.example.board.domain.Subject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectMapper {
    List<Subject> getSubjectList();
    List<Subject> getParentsSubjectList();
    List<Subject> getChildSubjectList(long parent_seq);
}
