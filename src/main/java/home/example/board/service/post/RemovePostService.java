package home.example.board.service.post;

import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.PostHistoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemovePostService {

    private final PostDAO postDAO;
    private final PostHistoryDAO postHistoryDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public RemovePostService(PostDAO postDAO, PostHistoryDAO postHistoryDAO, OutboxDAO outboxDAO) {
        this.postDAO = postDAO;
        this.postHistoryDAO = postHistoryDAO;
        this.outboxDAO = outboxDAO;
    }

    @Transactional
    public void removePost(long post_seq) {
        if (!postDAO.isExistPost(post_seq)) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        // 게시물 삭제 이력 추가
        postHistoryDAO.insertPostHistory(post_seq);
        // 게시물 삭제
        postDAO.removePost(post_seq);
        // Outbox에 게시물 삭제 내용 추가
        // outboxDAO.insertPost(post_seq, "", "", "DELETE");
    }
}
