package home.example.board.service;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import home.example.board.repository.CommentMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public void insertComment(long post_seq, String content, Integer parent_comment_seq, long user_seq) {
        Comment comment = Comment.builder()
                .post_seq(post_seq)
                .content(content)
                .parent_comment_seq(parent_comment_seq)
                .user_seq(user_seq)
                .build();
        commentMapper.insertComment(comment);
    }

    public JSONObject selectComments(int post_seq) {
        JSONObject jsonObject = new JSONObject();
        List<CommentDTO> comments = commentMapper.selectComments(post_seq);
        jsonObject.put("comments", comments);
        jsonObject.put("comment_count", comments.size());
        return jsonObject;
    }
}
