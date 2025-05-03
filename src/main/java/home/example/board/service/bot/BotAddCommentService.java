package home.example.board.service.bot;

import home.example.board.DTO.botApiDTO.BotAddCommentDTO;
import home.example.board.dao.CommentDAO;
import home.example.board.dao.CommentHistoryDAO;
import home.example.board.dao.OutboxDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BotAddCommentService {

    private final CommentDAO commentDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public BotAddCommentService(
            CommentDAO commentDAO,
            CommentHistoryDAO commentHistoryDAO,
            OutboxDAO outboxDAO) {
        this.commentDAO = commentDAO;
        this.outboxDAO = outboxDAO;
    }

    @Transactional
    public void addCommentByBot(BotAddCommentDTO botAddCommentDTO, long user_seq) {
        System.out.println("botAddCommentDTO = " + botAddCommentDTO);

        long post_seq = botAddCommentDTO.getPost_seq();
        String content = botAddCommentDTO.getContent();

        Long parent_comment_seq = null;
        if(botAddCommentDTO.getParent_comment_seq() != null && botAddCommentDTO.getParent_comment_seq() != 0L)
            parent_comment_seq = botAddCommentDTO.getParent_comment_seq();

        Long reply_user_seq = null;
        if(botAddCommentDTO.getReply_user_seq() != null && botAddCommentDTO.getReply_user_seq() != 0L)
            reply_user_seq = botAddCommentDTO.getReply_user_seq();

        if(checkReplyParams(parent_comment_seq, reply_user_seq)){
            throw new IllegalArgumentException("댓글을 작성할 수 없습니다. parent_comment_seq와 reply_user_seq는 같이 사용해야 합니다.");
        }

        long comment_seq = commentDAO.insertComment(post_seq, content, parent_comment_seq, reply_user_seq, user_seq);
        outboxDAO.insertComment(comment_seq, content,"INSERT");
    }

    private boolean checkReplyParams(Long parent_comment_seq, Long reply_user_seq) {
        System.out.println("parent_comment_seq = " + parent_comment_seq+", reply_user_seq = " + reply_user_seq);
        // parent_comment_seq가 null이 아닐 경우 reply_user_seq도 null이 아닐 경우에만 댓글을 작성한다.
        if(parent_comment_seq != null){
            if(reply_user_seq == null) {
                return false;
            }
        }
        if(reply_user_seq != null){
            if(parent_comment_seq == null) {
                return false;
            }
        }
        return true;
    }
}
