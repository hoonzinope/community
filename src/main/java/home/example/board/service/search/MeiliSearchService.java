package home.example.board.service.search;

import home.example.board.DTO.PostPagingDTO;
import home.example.board.dao.PostDAO;
import home.example.board.dao.SubjectDAO;
import home.example.board.dao.UserDAO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeiliSearchService {

    @Value("${spring.meilisearch.url}")
    private String url;
    @Value("${spring.meilisearch.api-key}")
    private String apiKey;

    private final PostDAO postDAO;
    private final SubjectDAO subjectDAO;
    private final UserDAO userDAO;

    @Autowired
    public MeiliSearchService(PostDAO postDAO, SubjectDAO subjectDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.subjectDAO = subjectDAO;
        this.userDAO = userDAO;
    }

    public JSONObject search(String keyword, int offset, int limit, String type) {
        JSONObject result = new JSONObject();
        JSONObject searchResult = new JSONObject();

        try{
            List<Long> diableble_subject_list =
                    subjectDAO.getSubjectListUseN()
                            .stream()
                            .map(subject -> subject.getSubject_seq())
                            .collect(Collectors.toList());

            if(type.equalsIgnoreCase("comment")) {
                searchResult = getCommentSearch(keyword, offset, limit, diableble_subject_list);
            }else if(type.equalsIgnoreCase("post")) {
                searchResult = getPostSearch(keyword, offset, limit, diableble_subject_list);
            }else{
                throw new Exception("잘못된 검색 타입입니다.");
            }
            List<Long> postSeqList = getPostSeqList(searchResult);
            if(type.equalsIgnoreCase("comment")) {
                result.put("post_comment_list", getPostCommentList(postDAO.getPostListByPostSeqList(postSeqList), searchResult));
            }else{
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
            }
            result.put("searchResult", searchResult);
            result.put("total", getTotalSize(searchResult));
            result.put("keyword", keyword);
            result.put("offset", offset);
            result.put("limit", limit);
            result.put("type", type);
        }catch (Exception e){
            e.printStackTrace();
            result.put("error", "검색 중 오류가 발생했습니다."+ e.getMessage());
        }
        return result;
    }

    private JSONObject getPostSearch(String keyword, int offset, int limit, List<Long> diableble_subject_list) throws IOException, ParseException {
        String url = this.url + "/indexes/posts/search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // Perform the search operation using the MeiliSearch API
        HttpPost post = new HttpPost(url);

        // Set request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("q", keyword);
        requestBody.put("offset", offset);
        requestBody.put("limit", limit);
        if(diableble_subject_list.size() > 0) {
            List<String> filters = diableble_subject_list.stream().map(subject_seq -> {
                String condition = "subject_seq != " + subject_seq;
                return condition;
            }).collect(Collectors.toList());
            requestBody.put("filter", filters);
        }
        requestBody.put("sort", new ArrayList<>(Arrays.asList("insert_timestamp:desc")));

        // Execute the request
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "Bearer "+this.apiKey);
        post.setEntity(new StringEntity(requestBody.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }

    private JSONObject getCommentSearch(String keyword, int offset, int limit, List<Long> diableble_subject_list) throws IOException, ParseException {
        String url = this.url + "/indexes/comments/search";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        // Perform the search operation using the MeiliSearch API
        HttpPost post = new HttpPost(url);


        // Set request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("q", keyword);
        requestBody.put("offset", offset);
        requestBody.put("limit", limit);
        if(diableble_subject_list.size() > 0) {
            List<String> filters = diableble_subject_list.stream().map(subject_seq -> {
                String condition = "subject_seq != " + subject_seq;
                return condition;
            }).collect(Collectors.toList());
            requestBody.put("filter", filters);
        }
        requestBody.put("sort", new ArrayList<>(Arrays.asList("insert_timestamp:desc")));

        // Execute the request
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "Bearer "+this.apiKey);
        post.setEntity(new StringEntity(requestBody.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }

    private int getTotalSize(JSONObject searchResult) {
        if (searchResult != null && searchResult.containsKey("estimatedTotalHits")) {
            return Integer.parseInt(searchResult.get("estimatedTotalHits").toString());
        } else {
            return 0;
        }
    }

    private List<Long> getPostSeqList(JSONObject searchResult) {
        List<Long> postSeqList = new ArrayList<>();
        if (searchResult != null && searchResult.containsKey("hits")) {
            List<JSONObject> hits = (List<JSONObject>) searchResult.get("hits");
            for (JSONObject hit : hits) {
                if (hit.containsKey("post_seq")) {
                    postSeqList.add(Long.parseLong(hit.get("post_seq").toString()));
                }
            }
        }
        return postSeqList;
    }

    private JSONArray getPostCommentList(List<PostPagingDTO> posts, JSONObject commentSearchResult) {
        JSONArray postCommentList = new JSONArray();
        if (commentSearchResult != null && commentSearchResult.containsKey("hits")) {
            List<JSONObject> hits = (List<JSONObject>) commentSearchResult.get("hits");
            for (JSONObject hit : hits) {
                Long post_seq = Long.parseLong(hit.get("post_seq").toString());
                PostPagingDTO post = posts.stream().filter(postPagingDTO -> postPagingDTO.getPost_seq() == post_seq).findFirst().get();
                String user_nickname = userDAO.getUserBySeq((long) hit.get("user_seq")).getUser_nickname();
                hit.put("user_nickname", user_nickname);
                hit.put("post", post);

                postCommentList.add(hit);
            }
        }
        return postCommentList;
    }
}
