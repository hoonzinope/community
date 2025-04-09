package home.example.board.service.post;

import home.example.board.dao.PostDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckPostService {

    private final PostDAO postDAO;
    @Autowired
    public CheckPostService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    public boolean getPostByUser(long post_seq, long user_seq) {
        return postDAO.isPostByUser(post_seq, user_seq);
    }
}
