package home.example.board.service;

import home.example.board.DTO.CommentDTO;
import home.example.board.dao.OutboxDAO;
import home.example.board.domain.Comment;
import home.example.board.domain.CommentLike;
import home.example.board.domain.User;
import home.example.board.repository.CommentHistoryMapper;
import home.example.board.repository.CommentLikeMapper;
import home.example.board.repository.CommentMapper;
import home.example.board.repository.UserMapper;
import home.example.board.utils.NickNameUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CommentHistoryMapper commentHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OutboxDAO outboxDAO;

    @Transactional
    public void insertComment(long post_seq, String content, Long parent_comment_seq, long user_seq) {
        Comment comment = Comment.builder()
                .post_seq(post_seq)
                .content(content)
                .parent_comment_seq(parent_comment_seq)
                .user_seq(user_seq)
                .build();
        commentMapper.insertComment(comment);
        outboxDAO.insertComment(comment, "INSERT");
    }

    public JSONObject selectComments(long post_seq, long user_seq) {
        JSONObject jsonObject = new JSONObject();
        List<CommentDTO> comments = commentMapper.selectComments(post_seq);

        if(user_seq != -1) {
            checkLikeClick(user_seq, comments);
        }
        if (!comments.isEmpty()) {
            setUserNickName(comments);
        }

        jsonObject.put("comments", comments);
        jsonObject.put("comment_count", comments.size());
        return jsonObject;
    }

    private void checkLikeClick(long user_seq, List<CommentDTO> comments) {
        // get commentSeqList
        List<Long> commentSeqList = comments.stream().map(comment-> {
            long comment_seq = comment.getComment_seq();
            return comment_seq;
        }).collect(Collectors.toList());

        // get commentLikes by commentSeqList
        Map<String, Object> params = new HashMap<>();
        params.put("commentSeqList", commentSeqList);
        params.put("user_seq", user_seq);
        List<CommentLike> commentLikes = commentLikeMapper.selectCommentLikes(params);

        // check like click
        commentLikes.stream().forEach(commentLike -> {
            long comment_seq = commentLike.getComment_seq();
            comments.stream().forEach(comment -> {
                if(comment.getComment_seq() == comment_seq) {
                    comment.setClick_like(true);
                    comment.setClick_like_type(commentLike.getLike_type());
                }
            });
        });
    }

    private void setUserNickName(List<CommentDTO> comments) {
        List<Long> userSeqList = comments.stream().map(CommentDTO::getUser_seq)
                .collect(Collectors.toList());

        // get user list by userSeqList
        Map<Long, Integer> userSeqDeleteFlagMap = userMapper.getUserBySeqList(userSeqList).stream()
                .collect(Collectors.toMap(User::getUser_seq, User::getDelete_flag));
        Map<Long, String> userSeqNicknameMap = userMapper.getUserBySeqList(userSeqList).stream()
                .collect(Collectors.toMap(User::getUser_seq, User::getUser_nickname));
        // blind delete user nickname
        comments.stream().forEach(comment -> {
            long user_seq = comment.getUser_seq();
            long parent_user_seq = comment.getP_user_seq();

            if(userSeqDeleteFlagMap.get(user_seq) == 1) {
                comment.setUser_name("비활성 사용자");
            }else{
                String nickname = userSeqNicknameMap.get(user_seq);
                nickname = NickNameUtils.nickNameTrim(nickname);
                comment.setUser_name(nickname);
            }

            if(parent_user_seq != 0) {
                if(userSeqDeleteFlagMap.get(parent_user_seq) == 1) {
                    comment.setP_user_name("비활성 사용자");
                }else{
                    String nickname = userSeqNicknameMap.get(parent_user_seq);
                    nickname = NickNameUtils.nickNameTrim(nickname);
                    comment.setP_user_name(nickname);
                }
            }else{
                comment.setP_user_name("");
            }
        });
    }

    @Transactional
    public void updateComment(long comment_seq, String content, long user_seq) throws IllegalAccessException {
        Comment comment = commentMapper.selectComment(comment_seq);
        if(user_seq != comment.getUser_seq()) {
            throw new IllegalAccessException("user not matched");
        }
        commentHistoryMapper.insertCommentHistory(comment);
        comment.setContent(content);
        commentMapper.updateComment(comment);
        outboxDAO.insertComment(comment, "UPDATE");
    }

    @Transactional
    public void deleteComment(long comment_seq, long user_seq) throws IllegalAccessException {
        Comment comment = commentMapper.selectComment(comment_seq);
        if(user_seq != comment.getUser_seq()) {
            throw new IllegalAccessException("user not matched");
        }
        commentHistoryMapper.insertCommentHistory(comment);
        commentMapper.deleteComment(comment_seq);
        outboxDAO.insertComment(comment, "DELETE");
    }
}
