package home.example.board.service.post;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.config.MinioConfig;
import home.example.board.dao.PostDAO;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadPostService {

    private final PostDAO postDAO;

    private final MinioConfig minioConfig;

    @Autowired
    public ReadPostService(PostDAO postDAO, MinioConfig minioConfig) {
        this.postDAO = postDAO;
        this.minioConfig = minioConfig;
    }

    public JSONObject getPost(long post_seq) {
        PostPagingDTO post =  postDAO.getPost(post_seq);
        JSONObject postObject = new JSONObject();
        postObject.put("post", postDtoToJson(post));
        return postObject;
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
                    return postDtoToJson(post);
                })
                .collect(Collectors.toList());
    }

    private String convertImageServername(String content) {
        return content.replace("http://imageStorage", minioConfig.getMinioUrl());
    }

    @NotNull
    private String getContentHtml(String rawContent) {
        List<String> allowedHosts = Arrays.asList(
                "www.youtube.com", "youtube.com", "youtu.be",
                "vimeo.com", "vine.co", "instagram.com",
                "dailymotion.com", "youku.com"
        );
        Safelist safelist = Safelist.basicWithImages()
                .addTags("iframe")
                .addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen");
        String cleanedContent = Jsoup.clean(rawContent, safelist);
        Document doc = Jsoup.parseBodyFragment(cleanedContent);
        Elements iframes = doc.select("iframe");
        for (Element iframe : iframes) {
            String src = iframe.attr("src");
            try {
                URL url = new URL(src);
                String host = url.getHost().toLowerCase();
                if (!allowedHosts.contains(host)) {
                    iframe.remove();
                }
            } catch (MalformedURLException e) {
                iframe.remove();
            }
        }
        return doc.body().html();
    }

    private JSONObject postDtoToJson(PostPagingDTO post) {
        JSONObject postJson = new JSONObject();
        postJson.put("post_seq", post.getPost_seq());
        postJson.put("title", post.getTitle());
        // Convert image server name in content
        String content = post.getContent();
        content = getContentHtml(content);
        content = convertImageServername(content);
        postJson.put("content", content);
        postJson.put("insert_ts", post.getInsert_ts());
        postJson.put("update_ts", post.getUpdate_ts());
        postJson.put("view_count", post.getView_count());
        postJson.put("user_seq", post.getUser_seq());
        postJson.put("user_nickname", post.getUser_nickname());
        postJson.put("subject_seq", post.getSubject_seq());
        postJson.put("category", post.getCategory());
        postJson.put("like_count", post.getLike_count());
        postJson.put("dislike_count", post.getDislike_count());

        return postJson;
    }

    public JSONObject getSeenPostListByPostSeqList(List<Long> postSeqList) {
        List<PostPagingDTO> postListPaging = postDAO.getSeenPostListByPostSeqList(postSeqList);
        List<JSONObject> postList = convertToJsonList(postListPaging);

        JSONObject result = new JSONObject();
        result.put("postList", postList);

        return result;
    }
}
