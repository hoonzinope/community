package home.example.board.service;

import home.example.board.domain.PostLike;
import home.example.board.repository.PostLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PostLikeService {

    @Autowired
    PostLikeMapper postLikeMapper;

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

    public int countPostLike(long post_seq, String like_type) {
        return postLikeMapper.countPostLike(post_seq, like_type);
    }

    public PostLike getPostLike(long post_seq, long user_seq) {
        PostLike postLike = PostLike.builder()
                .post_seq(post_seq)
                .user_seq(user_seq)
                .build();
        return postLikeMapper.selectPostLike(postLike);
    }

}
