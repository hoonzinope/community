package home.example.board.service.post;

import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.PostHistoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddPostService {

    private final PostDAO postDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public AddPostService(
            PostDAO postDAO,
            OutboxDAO outboxDAO) {
        this.postDAO = postDAO;
        this.outboxDAO = outboxDAO;
    }

    @Transactional
    public void addPost(String title, String content, long user_seq, int subject_seq) {
        long post_seq = postDAO.addPost(title, content, user_seq, subject_seq);
        outboxDAO.insertPost(post_seq,title,content,"INSERT");
    }
}
