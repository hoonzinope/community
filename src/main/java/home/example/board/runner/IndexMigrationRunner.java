package home.example.board.runner;

import home.example.board.repository.CommentMapper;
import home.example.board.repository.PostMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(name = "spring.elasticsearch.migration.enabled", havingValue = "true")
public class IndexMigrationRunner implements CommandLineRunner {

    @Autowired
    PostMapper postMapper;

    @Autowired
    CommentMapper commentMapper;

    @Value("${spring.elasticsearch.uris}")
    String es_url;

    @Override
    public void run(String... args) throws Exception {
        migratePostIndex();
        migrateCommentIndex();
    }

    public void migratePostIndex() {
        postMapper.getPostAll().forEach(post -> {
            String title = post.getTitle();
            String content = post.getContent();
            boolean delete_flag = post.isDelete_flag();
            long user_seq = post.getUser_seq();
            long subject_seq = post.getSubject_seq();

            // Perform the index migration logic here
            // title, content, post_seq, delete_flag, subject_seq, user_seq
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("post_seq", post.getPost_seq());
            jsonObject.put("delete_flag", delete_flag);
            jsonObject.put("subject_seq", subject_seq);
            jsonObject.put("user_seq", user_seq);

            String ES_URL = es_url + "/posts/post/" + post.getPost_seq();
            // HttpClient 생성
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // HTTP POST 요청 생성
                HttpPost httpPost = new HttpPost(ES_URL);

                // 인덱싱할 JSON 문서 생성
                StringEntity entity = new StringEntity(jsonObject.toJSONString(), "UTF-8");
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");

                // 요청 실행
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    System.out.println("응답 상태: " + response.getStatusLine());

                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) {
                        String responseString = EntityUtils.toString(responseEntity, "UTF-8");
                        System.out.println("응답 내용: " + responseString);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void migrateCommentIndex() {
        commentMapper.getCommentAll().forEach(comment -> {
            String content = comment.getContent();
            String insert_ts = comment.getInsert_ts().toString();
            String delete_ts = comment.getDelete_ts() != null ? comment.getDelete_ts().toString() : null;
            boolean delete_flag = comment.getDelete_flag() != 0;
            long user_seq = comment.getUser_seq();
            long post_seq = comment.getPost_seq();
            long comment_seq = comment.getComment_seq();
            long parent_comment_seq = comment.getParent_comment_seq() == null ? 0L : comment.getParent_comment_seq();

            // Perform the index migration logic here
            // content, comment_seq, delete_flag, post_seq, user_seq, parent_comment_seq, insert_ts, delete_ts
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            jsonObject.put("comment_seq", comment_seq);
            jsonObject.put("delete_flag", delete_flag);
            jsonObject.put("post_seq", post_seq);
            jsonObject.put("user_seq", user_seq);
            jsonObject.put("parent_comment_seq", parent_comment_seq);
            jsonObject.put("insert_ts", insert_ts);
            jsonObject.put("delete_ts", delete_ts);
            String ES_URL = es_url + "/comments/comment/" + comment.getComment_seq();

            // HttpClient 생성
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // HTTP POST 요청 생성
                HttpPost httpPost = new HttpPost(ES_URL);

                // 인덱싱할 JSON 문서 생성
                StringEntity entity = new StringEntity(jsonObject.toJSONString(), "UTF-8");
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");

                // 요청 실행
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    System.out.println("응답 상태: " + response.getStatusLine());

                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) {
                        String responseString = EntityUtils.toString(responseEntity, "UTF-8");
                        System.out.println("응답 내용: " + responseString);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
