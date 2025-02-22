package home.example.board.service;

import home.example.board.domain.CommentLike;
import home.example.board.repository.CommentLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    public void insertCommentLike(int comment_seq, int user_seq, String like_type) {
        CommentLike commentLike = CommentLike.builder()
                .comment_seq(comment_seq)
                .user_seq(user_seq)
                .like_type(like_type.equals("like") ? 1 : 0)
                .insert_ts(LocalDateTime.now())
                .build();
        commentLikeMapper.insertCommentLike(commentLike);
    }

    public void deleteCommentLike(int comment_seq, int user_seq) {
        CommentLike commentLike = CommentLike.builder()
                .comment_seq(comment_seq)
                .user_seq(user_seq)
                .build();
        commentLikeMapper.deleteCommentLike(commentLike);
    }
}
