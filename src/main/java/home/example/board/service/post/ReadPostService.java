package home.example.board.service.post;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.dao.PostDAO;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadPostService {

    private final PostDAO postDAO;

    @Autowired
    public ReadPostService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    public JSONObject getPost(long post_seq) {
        return postDAO.getPost(post_seq);
    }

    public boolean getPostByUser(long post_seq, long user_seq) {
        return postDAO.isPostByUser(post_seq, user_seq);
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

    public JSONObject getUserPostListPaging(Long user_seq, int offset, int limit) {
        int postTotalSize = postDAO.getUserPostTotalSize(user_seq);
        List<PostPagingDTO> postListPaging = postDAO.getUserPostList(user_seq, offset, limit);
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
