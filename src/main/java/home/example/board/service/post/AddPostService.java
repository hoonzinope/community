package home.example.board.service.post;

import home.example.board.DTO.botLoginDTO.BotAddPostDTO;
import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.PostHistoryDAO;
import home.example.board.dao.SubjectDAO;
import home.example.board.domain.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddPostService {

    private final PostDAO postDAO;
    private final OutboxDAO outboxDAO;
    private final SubjectDAO subjectDAO;

    @Autowired
    public AddPostService(
            PostDAO postDAO,
            OutboxDAO outboxDAO, SubjectDAO subjectDAO) {
        this.postDAO = postDAO;
        this.outboxDAO = outboxDAO;
        this.subjectDAO = subjectDAO;
    }

    @Transactional
    public void addPost(String title, String content, long user_seq, long subject_seq) {
        long post_seq = postDAO.addPost(title, content, user_seq, subject_seq);
        outboxDAO.insertPost(post_seq,title,content,"INSERT");
    }

    public void addPostByBot(BotAddPostDTO botAddPostDTO, long user_seq) {
        String title = botAddPostDTO.getTitle();
        String content = botAddPostDTO.getContent();
        String subject_name = botAddPostDTO.getSubject();
        Subject subject = subjectDAO.getSubjectByName(subject_name);
        this.addPost(title, content, user_seq, subject.getSubject_seq());
    }
}
