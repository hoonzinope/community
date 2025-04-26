package home.example.board.service;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.dao.PostDAO;
import home.example.board.domain.PostLike;
import home.example.board.repository.PostLikeMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostLikeService {

    @Autowired
    PostLikeMapper postLikeMapper;

    @Autowired
    PostDAO postDAO;

    public void addPostLike(long post_seq, long user_seq, String like_type) {
        PostLike postLike = PostLike.builder()
                .post_seq(post_seq)
                .user_seq(user_seq)
                .like_type(like_type)
                .build();
        postLikeMapper.insertPostLike(postLike);
    }

    public void removePostLike(long post_seq, long user_seq) {
        PostLike postLike = PostLike.builder()
                .post_seq(post_seq)
                .user_seq(user_seq)
                .build();
        postLikeMapper.deletePostLike(postLike);
    }

    public PostLike getPostLike(long post_seq, long user_seq) {
        PostLike postLike = PostLike.builder()
                .post_seq(post_seq)
                .user_seq(user_seq)
                .build();
        return postLikeMapper.selectPostLike(postLike);
    }

    public JSONObject getLikePostsByUser(long user_seq, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_seq", user_seq);
        params.put("offset", offset);
        params.put("limit", limit);
        List<PostLike> postLikes = postLikeMapper.selectLikePostsByUser(params);

        List<Long> postSeqList = postLikes.stream().map(PostLike::getPost_seq).collect(Collectors.toList());
        List<PostPagingDTO> postListByPostSeqList = postDAO.getPostListByPostSeqList(postSeqList);
        int total = postLikeMapper.selectLikePostsCountByUser(user_seq);

        JSONArray postList = new JSONArray();
        postListByPostSeqList.forEach(postPagingDTO -> {
            JSONObject postJson = new JSONObject();
            postJson.put("post_seq", postPagingDTO.getPost_seq());
            postJson.put("title", postPagingDTO.getTitle());
            postJson.put("content", postPagingDTO.getContent());
            postJson.put("user_seq", postPagingDTO.getUser_seq());
            postJson.put("subject_seq", postPagingDTO.getSubject_seq());
            postJson.put("insert_ts", postPagingDTO.getInsert_ts());
            postList.add(postJson);
        });

        JSONObject result = new JSONObject();
        result.put("postList", postList);
        result.put("total", total);
        result.put("size", postList.size());

        return result;
    }

    public JSONObject getDisLikePostsByUser(long user_seq, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_seq", user_seq);
        params.put("offset", offset);
        params.put("limit", limit);
        List<PostLike> postLikes = postLikeMapper.selectDisLikePostsByUser(params);

        List<Long> postSeqList = postLikes.stream().map(PostLike::getPost_seq).collect(Collectors.toList());
        List<PostPagingDTO> postListByPostSeqList = postDAO.getPostListByPostSeqList(postSeqList);
        int total = postLikeMapper.selectDisLikePostsCountByUser(user_seq);

        JSONArray postList = new JSONArray();
        postListByPostSeqList.forEach(postPagingDTO -> {
            JSONObject postJson = new JSONObject();
            postJson.put("post_seq", postPagingDTO.getPost_seq());
            postJson.put("title", postPagingDTO.getTitle());
            postJson.put("content", postPagingDTO.getContent());
            postJson.put("user_seq", postPagingDTO.getUser_seq());
            postJson.put("subject_seq", postPagingDTO.getSubject_seq());
            postJson.put("insert_ts", postPagingDTO.getInsert_ts());
            postList.add(postJson);
        });

        JSONObject result = new JSONObject();
        result.put("postList", postList);
        result.put("total", total);
        result.put("size", postList.size());

        return result;
    }

}
