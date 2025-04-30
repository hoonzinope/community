package home.example.board.service.bot;

import home.example.board.DTO.botApiDTO.BotAddPostDTO;
import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.SubjectDAO;
import home.example.board.domain.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotAddPostService {

    private final PostDAO postDAO;
    private final OutboxDAO outboxDAO;
    private final SubjectDAO subjectDAO;

    @Autowired
    public BotAddPostService(
            PostDAO postDAO,
            OutboxDAO outboxDAO, SubjectDAO subjectDAO) {
        this.postDAO = postDAO;
        this.outboxDAO = outboxDAO;
        this.subjectDAO = subjectDAO;
    }

    public void addPostByBot(BotAddPostDTO botAddPostDTO, long user_seq) {
        String title = botAddPostDTO.getTitle();
        String content = botAddPostDTO.getContent();
        String subject_name = botAddPostDTO.getSubject();
        Subject subject = subjectDAO.getSubjectByName(subject_name);

        long post_seq = postDAO.addPost(title, content, user_seq, subject.getSubject_seq());
        outboxDAO.insertPost(post_seq,title,content,"INSERT");
    }

}
