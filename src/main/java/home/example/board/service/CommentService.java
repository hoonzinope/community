package home.example.board.service;

import home.example.board.DTO.CommentDTO;
import home.example.board.domain.Comment;
import home.example.board.domain.CommentLike;
import home.example.board.repository.CommentLikeMapper;
import home.example.board.repository.CommentMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    public void insertComment(long post_seq, String content, Integer parent_comment_seq, long user_seq) {
        Comment comment = Comment.builder()
                .post_seq(post_seq)
                .content(content)
                .parent_comment_seq(parent_comment_seq)
                .user_seq(user_seq)
                .build();
        commentMapper.insertComment(comment);
    }

    public JSONObject selectComments(int post_seq, int user_seq) {
        JSONObject jsonObject = new JSONObject();
        List<CommentDTO> comments = commentMapper.selectComments(post_seq);
        if(user_seq != -1) {
            List<Integer> commentSeqList = comments.stream().map(comment-> {
                int comment_seq = comment.getComment_seq();
                return comment_seq;
            }).collect(Collectors.toList());
            Map<String, Object> params = new HashMap<>();
            params.put("commentSeqList", commentSeqList);
            params.put("user_seq", user_seq);
            List<CommentLike> commentLikes = commentLikeMapper.selectCommentLikes(params);

            commentLikes.stream().forEach(commentLike -> {
                int comment_seq = commentLike.getComment_seq();
                comments.stream().forEach(comment -> {
                    if(comment.getComment_seq() == comment_seq) {
                        comment.setClick_like(true);
                        comment.setClick_like_type(commentLike.getLike_type());
                    }
                });
            });
        }
        jsonObject.put("comments", comments);
        jsonObject.put("comment_count", comments.size());
        return jsonObject;
    }

}
