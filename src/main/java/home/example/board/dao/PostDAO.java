package home.example.board.dao;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.domain.Post;
import home.example.board.domain.User;
import home.example.board.repository.PostMapper;
import home.example.board.repository.UserMapper;
import home.example.board.utils.NickNameUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostDAO {

    @Autowired
    PostMapper postMapper;

    public List<PostPagingDTO> getPostListPaging(int offset, int limit, long subject_seq) {
        // offset: 시작 위치, limit: 가져올 데이터 개수
        Map<String, Object> paging = new HashMap<>();
        paging.put("offset", offset);
        paging.put("limit", limit);
        if(subject_seq != 0) {
            paging.put("subject_seq", subject_seq);
            return postMapper.getPostListByCategory(paging);
        }
        return postMapper.getPostListPaging(paging);
    }

    public int getPostTotalSize(long subject_seq) {
        // 전체 게시물 개수
        Map<String, Object> requestMap = new HashMap<>();
        if(subject_seq != 0) {
            requestMap.put("subject_seq", subject_seq);
            return postMapper.getPostTotalSizeByCategory(requestMap);
        }
        return postMapper.getPostTotalSize();
    }

    public PostPagingDTO getPost(long post_seq) {
        // 게시물 상세 조회
        PostPagingDTO post = postMapper.getPost(post_seq);
        if(post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }
        postMapper.updateViewCount(post_seq);
        return post;
    }

    public boolean isExistPost(long post_seq) {
        // 게시물 존재 여부 확인
        PostPagingDTO post = postMapper.getPost(post_seq);
        return post.getPost_seq() == post_seq;
    }

    public boolean isPostByUser(long post_seq, long user_seq) {
        // 게시물 작성자와 현재 사용자가 같은지 확인
        PostPagingDTO post = postMapper.getPost(post_seq);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }
        return post.getUser_seq() == user_seq;
    }

    public List<PostPagingDTO> getUserPostList(long user_seq, int offset, int limit) {
        // 특정 사용자가 작성한 게시물 목록 조회
        Map<String, Object> paging = new HashMap<>();
        paging.put("offset", offset);
        paging.put("limit", limit);
        paging.put("user_seq", user_seq);
        List<PostPagingDTO> postListPaging = postMapper.getUserPostListPaging(paging);
        return postListPaging;
    }

    public int getUserPostTotalSize(long user_seq) {
        // 특정 사용자가 작성한 게시물 개수 조회
        Map<String, Object> paging = new HashMap<>();
        paging.put("user_seq", user_seq);
        return postMapper.getUserPostTotalSize(paging);
    }

    public List<PostPagingDTO> getPostListByPostSeqList(List<Long> post_seq_list) {
        // 게시물 목록 조회
        if(post_seq_list == null || post_seq_list.isEmpty()) {
            return new ArrayList<PostPagingDTO>();
        }
        List<PostPagingDTO> postList = postMapper.getPostList(post_seq_list);
        return postList;
    }

    public Long addPost(String title, String content, long user_seq, int subject_seq) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .user_seq(user_seq)
                .subject_seq(subject_seq)
                .build();
        postMapper.insertPost(post);
        return post.getPost_seq();
    }

    public void modifyPost(long post_seq, String title, String content) {
        Post post = postMapper.getPostById(post_seq);
        if(post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }else{
            post.setTitle(title);
            post.setContent(content);
            postMapper.updatePost(post);
        }
    }

    public void removePost(long post_seq) {
        postMapper.deletePost(post_seq);
    }

    public List<PostPagingDTO> getSeenPostListByPostSeqList(List<Long> postSeqList) {
        // 게시물 목록 조회
        if(postSeqList == null || postSeqList.isEmpty()) {
            return new ArrayList<PostPagingDTO>();
        }
        List<PostPagingDTO> postList = postMapper.getPostList(postSeqList);
        return postList;
    }
}
