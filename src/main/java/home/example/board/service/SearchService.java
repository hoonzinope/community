package home.example.board.service;

import home.example.board.dao.PostDAO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SearchService {

    @Value("${spring.elasticsearch.uris}")
    private String url;

    private final PostDAO postDAO;

    @Autowired
    public SearchService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    public JSONObject search(String keyword, int offset, int limit, String type) {
        JSONObject result = new JSONObject();
        JSONObject searchResult = new JSONObject();
        List<Long> postSeqList = new ArrayList<>();
        try {
            // 검색 요청을 생성 하고 실행
            // type == "all" 인 경우, posts와 comments를 모두 검색
            if(type.equalsIgnoreCase("all")) {
                searchResult = getPostSearchRequest(keyword, offset, limit);
                postSeqList = getPostSeqList(searchResult);
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
                result.put("total", getTotalSize(searchResult));
                result.put("keyword", keyword);
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("type", type);
            }
            // type == "title" 인 경우, posts의 title만 검색
            else if (type.equalsIgnoreCase("title")) {
                result = getPostTitleSearchRequest(keyword, offset, limit);
                postSeqList = getPostSeqList(searchResult);
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
                result.put("total", getTotalSize(searchResult));
                result.put("keyword", keyword);
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("type", type);
            }
            // type == "content" 인 경우, posts의 content만 검색
            else if (type.equalsIgnoreCase("content")) {
                result = getPostContentSearchRequest(keyword, offset, limit);
                postSeqList = getPostSeqList(searchResult);
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
                result.put("total", getTotalSize(searchResult));
                result.put("keyword", keyword);
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("type", type);
            }
            // type == "comment" 인 경우, comments만 검색
            else if (type.equalsIgnoreCase("comment")) {
                result = getCommentSearchRequest(keyword, offset, limit);
                postSeqList = getPostSeqList(searchResult);
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
                result.put("total", getTotalSize(searchResult));
                result.put("keyword", keyword);
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("type", type);
            }
            else {
                result = getPostSearchRequest(keyword, offset, limit);
                postSeqList = getPostSeqList(searchResult);
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
                result.put("total", getTotalSize(searchResult));
                result.put("keyword", keyword);
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("type", type);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "검색 중 오류가 발생했습니다.");
        }
        return result;
    }

    private List<Long> getPostSeqList(JSONObject result) {
        List<Long> postSeqList = new ArrayList<>();
        if(result.get("hits") != null) {
            JSONObject hits = (JSONObject) result.get("hits");
            if(hits.get("hits") != null) {
                JSONArray hitsArray = (JSONArray) hits.get("hits");
                for (Object o : hitsArray) {
                    JSONObject hit = (JSONObject) o;
                    JSONObject source = (JSONObject) hit.get("_source");
                    if(source != null) {
                        Long postSeq = (Long) source.get("post_seq");
                        postSeqList.add(postSeq);
                    }
                }
            }
        }
        return postSeqList;
    }

    private int getTotalSize(JSONObject result) {
        int totalSize = 0;
        if(result.get("hits") != null) {
            JSONObject hits = (JSONObject) result.get("hits");
            if(hits.get("total") != null) {
                Long total = (Long) hits.get("total");
                totalSize = (total).intValue();
            }
        }
        return totalSize;
    }

    /*
    GET /posts/post/_search
    { "query": { "multi_match": { "query": "검색어", "fields": ["title", "content"] } }, "_source": ["post_seq"], "from": 0, "size": 10 }
     */
    private JSONObject getPostSearchRequest(String keyword, int offset, int limit) throws IOException, ParseException {
        String url = this.url + "/posts/post/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject multiMatch = new JSONObject();
        multiMatch.put("query", keyword);
        multiMatch.put("fields", Arrays.asList("title", "content"));
        query.put("multi_match", multiMatch);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/json으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        //System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            //System.out.println(line);
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }

    private JSONObject getPostTitleSearchRequest(String keyword, int offset, int limit) throws IOException, ParseException {
        String url = this.url + "/posts/post/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject match = new JSONObject();
        match.put("title", keyword);
        query.put("match", match);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/json으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        //System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            //System.out.println(line);
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }

    private JSONObject getPostContentSearchRequest(String keyword, int offset, int limit) throws IOException, ParseException {
        String url = this.url + "/posts/post/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject match = new JSONObject();
        match.put("content", keyword);
        query.put("match", match);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/json으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        //System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            //System.out.println(line);
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }

    /*
    GET /comments/comment/_search
    { "query": { "match": { "content": "검색어" } }, "_source": ["content"], "from": 0, "size": 10 }
     */
    private JSONObject getCommentSearchRequest(String keyword, int offset, int limit) throws IOException, ParseException {
        String url = this.url + "/comments/comment/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject match = new JSONObject();
        match.put("content", keyword);
        query.put("match", match);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("collapse", new JSONObject().put("field", "post_seq"));
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/son으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));

        // 요청 실행 및 응답 처리
        HttpResponse response = httpClient.execute(post);
        //System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            //System.out.println(line);
            resultBuilder.append(line);
        }
        httpClient.close();

        // 응답을 JSON 객체로 변환
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resultBuilder.toString());
    }
}
