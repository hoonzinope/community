package home.example.board.service;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.dao.OutboxDAO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.PostHistoryDAO;
import home.example.board.domain.Post;
import home.example.board.repository.PostHistoryMapper;
import home.example.board.repository.PostMapper;
import home.example.board.repository.UserMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostDAO postDAO;
    private final PostHistoryDAO postHistoryDAO;
    private final OutboxDAO outboxDAO;

    @Autowired
    public PostService(
            PostDAO postDAO,
            PostHistoryDAO postHistoryDAO,
            OutboxDAO outboxDAO
    ) {
        this.postDAO = postDAO;
        this.postHistoryDAO = postHistoryDAO;
        this.outboxDAO = outboxDAO;
    }

    public JSONObject getPostListPaging(int offset, int limit, Long subject_seq) {
        int postTotalSize = postDAO.getPostTotalSize(subject_seq);
        List<PostPagingDTO> postListPaging = postDAO.getPostListPaging(offset, limit, subject_seq);
        List<JSONObject> postList = convertToJsonList(postListPaging);

        JSONObject result = new JSONObject();
        result.put("total", postTotalSize);
        result.put("size", postList.size());
        result.put("postList", postList);

        return result;
    }

    public JSONObject getPost(long post_seq) {
        return postDAO.getPost(post_seq);
    }

    public boolean getPostByUser(long post_seq, long user_seq) {
        return postDAO.isPostByUser(post_seq, user_seq);
    }

    @Transactional
    public void addPost(String title, String content, long user_seq, int subject_seq) {
        long post_seq = postDAO.addPost(title, content, user_seq, subject_seq);
        outboxDAO.insertPost(post_seq,title,content,"INSERT");
    }

    @Transactional
    public void modifyPost(long post_seq, String title, String content) {
        if(!postDAO.isExistPost(post_seq)) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            postHistoryDAO.insertPostHistory(post_seq);
            postDAO.modifyPost(post_seq, title, content);
            outboxDAO.insertPost(post_seq, title, content, "UPDATE");
        }
    }

    @Transactional
    public void removePost(long post_seq) {
        if(!postDAO.isExistPost(post_seq)) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            postHistoryDAO.insertPostHistory(post_seq);
            postDAO.removePost(post_seq);
            outboxDAO.insertPost(post_seq, "", "", "DELETE");
        }
    }

    public JSONObject getUserPostListPaging(long userSeq, int offset, int limit) {
        int postTotalSize = postDAO.getUserPostTotalSize(userSeq);
        List<PostPagingDTO> postListPaging = postDAO.getUserPostList(userSeq, offset, limit);

        List<JSONObject> postList = convertToJsonList(postListPaging);

        JSONObject result = new JSONObject();
        result.put("total", postTotalSize);
        result.put("size", postList.size());
        result.put("postList", postList);

        return result;
    }

    private List<JSONObject> convertToJsonList(List<PostPagingDTO> postList) {
        // List<PostPagingDTO>를 List<JSONObject>로 변환
        return postList.stream()
                .map(post -> {
                    JSONObject postJson = new JSONObject();
                    postJson.put("post_seq", post.getPost_seq());
                    postJson.put("title", post.getTitle());
                    postJson.put("content", post.getContent());
                    postJson.put("insert_ts", post.getInsert_ts());
                    postJson.put("update_ts", post.getUpdate_ts());
                    postJson.put("view_count", post.getView_count());
                    postJson.put("user_seq", post.getUser_seq());
                    postJson.put("category", post.getCategory());
                    postJson.put("like_count", post.getLike_count());
                    postJson.put("dislike_count", post.getDislike_count());
                    return postJson;
                })
                .collect(Collectors.toList());
    }
}
