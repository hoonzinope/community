package home.example.board.service;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.domain.Post;
import home.example.board.domain.Subject;
import home.example.board.domain.User;
import home.example.board.repository.PostHistoryMapper;
import home.example.board.repository.PostMapper;
import home.example.board.repository.SubjectMapper;
import home.example.board.repository.UserMapper;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    PostMapper postMapper;

    @Autowired
    PostHistoryMapper postHistoryMapper;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    UserMapper userMapper;

    public JSONObject getPostListPaging(int offset, int limit) {
        Map<String, Object> paging = new HashMap<>();
        paging.put("offset", offset);
        paging.put("limit", limit);
        int postTotalSize = postMapper.getPostTotalSize();
        List<PostPagingDTO> postListPaging = postMapper.getPostListPaging(paging);

        List<JSONObject> postList = postListPaging.stream()
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
                }).collect(Collectors.toList());

        JSONObject result = new JSONObject();
        result.put("total", postTotalSize);
        result.put("size", postList.size());
        result.put("postList", postList);

        return result;
    }

    public JSONObject getPostView(long post_seq) {
        Post post = postMapper.getPost(post_seq);
        if(post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            postMapper.updateViewCount(post_seq);
        }

        return getPost(post_seq);
    }

    public JSONObject getPost(long post_seq) {
        Post post = postMapper.getPost(post_seq);
        Map<Long, String> subjectMap = subjectService.getSubjectMap();

        User user = userMapper.getUserBySeq(post.getUser_seq());
        String userNickname = user.getUser_nickname().trim();
        int dashIndex = userNickname.indexOf("-");
        if (dashIndex != -1) {
            userNickname = userNickname.substring(0, dashIndex).trim();
        }

        JSONObject postJson = new JSONObject();
        postJson.put("post_seq", post.getPost_seq());
        postJson.put("title", post.getTitle());
        postJson.put("content", Jsoup.clean(post.getContent(), Safelist.basicWithImages()));
        postJson.put("view_count", post.getView_count());
        postJson.put("insert_ts", post.getInsert_ts());
        postJson.put("update_ts", post.getUpdate_ts());
        postJson.put("user_seq", post.getUser_seq());
        postJson.put("user_nickname", userNickname);
        postJson.put("category", subjectMap.get(post.getSubject_seq()));
        postJson.put("category_seq", post.getSubject_seq());
        postJson.put("like_count", postLikeService.countPostLike(post_seq, "LIKE"));
        postJson.put("dislike_count", postLikeService.countPostLike(post_seq, "DISLIKE"));

        JSONObject result = new JSONObject();
        result.put("post", postJson);

        return result;
    }

    public boolean getPostByUser(long post_seq, long user_seq) {
        Post post = postMapper.getPost(post_seq);
        return post.getUser_seq() == user_seq;
    }

    public void addPost(String title, String content, long user_seq, int subject_seq) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .user_seq(user_seq)
                .subject_seq(subject_seq)
                .build();
        postMapper.insertPost(post);
    }

    public void modifyPost(long post_seq, String title, String content) {
        Post post = postMapper.getPost(post_seq);
        if(post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            postHistoryMapper.insertPostHistory(post);
            post.setTitle(title);
            post.setContent(content);
            postMapper.updatePost(post);
        }
    }

    public void removePost(long post_seq) {
        Post post = postMapper.getPost(post_seq);
        if(post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            postHistoryMapper.insertPostHistory(post);
            postMapper.deletePost(post_seq);
        }
    }
}
