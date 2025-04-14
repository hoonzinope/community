package home.example.board.service.comment;

import home.example.board.DTO.CommentDTO;
import home.example.board.dao.CommentDAO;
import home.example.board.dao.CommentHistoryDAO;
import home.example.board.dao.OutboxDAO;
import home.example.board.domain.CommentLike;
import home.example.board.domain.User;
import home.example.board.repository.CommentLikeMapper;
import home.example.board.repository.UserMapper;
import home.example.board.utils.NickNameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReadCommentService {

    private final CommentDAO commentDAO;
    private final CommentLikeMapper commentLikeMapper;
    private final UserMapper userMapper;

    @Autowired
    public ReadCommentService(CommentDAO commentDAO, CommentLikeMapper commentLikeMapper, UserMapper userMapper) {
        this.commentDAO = commentDAO;
        this.commentLikeMapper = commentLikeMapper;
        this.userMapper = userMapper;
    }

    public JSONObject selectComments(long post_seq, long user_seq) {
        List<CommentDTO> comments = commentDAO.selectComments(post_seq);

//        if (!comments.isEmpty()) {
//            setUserNickName(comments);
//        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comments", comments);
        jsonObject.put("comment_count", comments.size());
        if(user_seq != -1)
            jsonObject.put("user_comment_click", checkLikeClick(user_seq, comments));
        return jsonObject;
    }

    private Map<Long, Object> checkLikeClick(long user_seq, List<CommentDTO> comments) {
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
        Map<Long, Object> resultMap = new HashMap<>();
        comments.forEach(comment -> {
            long comment_seq = comment.getComment_seq();
            boolean click_like = false;
            int click_like_type = 0;

            for(CommentLike commentLike : commentLikes) {
                if(commentLike.getComment_seq() == comment_seq) {
                    click_like = true;
                    click_like_type = commentLike.getLike_type();
                    break;
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comment_seq", comment_seq);
            jsonObject.put("click_like", click_like);
            jsonObject.put("click_like_type", click_like_type);
            resultMap.put(comment_seq, Arrays.asList(click_like, click_like_type));
        });
        return resultMap;
    }

//    private void setUserNickName(List<CommentDTO> comments) {
//        List<Long> userSeqList = comments.stream().map(CommentDTO::getUser_seq)
//                .collect(Collectors.toList());
//
//        // get user list by userSeqList
//        Map<Long, Integer> userSeqDeleteFlagMap = userMapper.getUserBySeqList(userSeqList).stream()
//                .collect(Collectors.toMap(User::getUser_seq, User::getDelete_flag));
//        Map<Long, String> userSeqNicknameMap = userMapper.getUserBySeqList(userSeqList).stream()
//                .collect(Collectors.toMap(User::getUser_seq, User::getUser_nickname));
//        // blind delete user nickname
//        comments.stream().forEach(comment -> {
//            long user_seq = comment.getUser_seq();
//            long parent_user_seq = comment.getP_user_seq();
//
//            if(userSeqDeleteFlagMap.get(user_seq) == 1) {
//                comment.setUser_name("비활성 사용자");
//            }else{
//                String nickname = userSeqNicknameMap.get(user_seq);
//                nickname = NickNameUtils.nickNameTrim(nickname);
//                comment.setUser_name(nickname);
//            }
//
//            if(parent_user_seq != 0) {
//                if(userSeqDeleteFlagMap.get(parent_user_seq) == 1) {
//                    comment.setP_user_name("비활성 사용자");
//                }else{
//                    String nickname = userSeqNicknameMap.get(parent_user_seq);
//                    nickname = NickNameUtils.nickNameTrim(nickname);
//                    comment.setP_user_name(nickname);
//                }
//            }else{
//                comment.setP_user_name("");
//            }
//        });
//    }
}
