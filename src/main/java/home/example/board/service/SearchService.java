package home.example.board.service;

import home.example.board.dao.PostDAO;
import home.example.board.dao.SubjectDAO;
import home.example.board.domain.Subject;
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

    private final SubjectDAO subjectDAO;

    @Autowired
    public SearchService(PostDAO postDAO, SubjectDAO subjectDAO) {
        this.postDAO = postDAO;
        this.subjectDAO = subjectDAO;
    }

    public JSONObject search(
            String keyword,
            int offset, int limit,
            String type) {
        JSONObject result = new JSONObject();
        JSONObject searchResult = new JSONObject();
        try {
            JSONObject match = buildMatch(type, keyword);
            if(type.equalsIgnoreCase("comment")) {
                searchResult = getCommentSearchRequest(keyword, offset, limit, match);
            }else{
                searchResult = getPostSearchRequest(keyword, offset, limit, match);
            }
            List<Long> postSeqList = getPostSeqList(searchResult);
            result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
            result.put("total", getTotalSize(searchResult));
            result.put("keyword", keyword);
            result.put("offset", offset);
            result.put("limit", limit);
            result.put("type", type);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "검색 중 오류가 발생했습니다.");
        }
        return result;
    }

    public JSONObject search(
            String keyword,
            int offset, int limit,
            String type, long subject_seq) {
        JSONObject result = new JSONObject();
        List<Subject> siblingSubject = subjectDAO.getSiblingSubject(subject_seq);
        JSONObject searchResult = new JSONObject();
        try {
            JSONObject match = buildMatch(type, keyword);
            if(type.equalsIgnoreCase("comment")) {
                searchResult = getCommentSearchRequestWithSubject(keyword, offset, limit, siblingSubject, match);
            }else{
                searchResult = getPostSearchRequestWithSubject(keyword, offset, limit, siblingSubject, match);
            }
            List<Long> postSeqList = getPostSeqList(searchResult);
            result.put("postList", postDAO.getPostListByPostSeqList(postSeqList));
            result.put("total", getTotalSize(searchResult));
            result.put("keyword", keyword);
            result.put("offset", offset);
            result.put("limit", limit);
            result.put("type", type);
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

    private JSONObject buildMatch(String type, String keyword) {
        JSONObject query = new JSONObject();
        JSONObject match = new JSONObject();
        if(type.equalsIgnoreCase("title")) {
            match.put("title", keyword);
            query.put("match", match);
        }else if (type.equalsIgnoreCase("content")) {
            match.put("content", keyword);
            query.put("match", match);
        }else if (type.equalsIgnoreCase("comment")) {
            match.put("content", keyword);
            query.put("match", match);
        }else if (type.equalsIgnoreCase("all")) {
            JSONObject multiMatch = new JSONObject();
            multiMatch.put("query", keyword);
            multiMatch.put("fields", Arrays.asList("title", "content"));
            query.put("multi_match", multiMatch);
        }
        return query;
    }

    /*
    GET /posts/post/_search
    { "query": { "multi_match": { "query": "검색어", "fields": ["title", "content"] } }, "_source": ["post_seq"], "from": 0, "size": 10 }
     */
    private JSONObject getPostSearchRequest(String keyword, int offset, int limit, JSONObject query) throws IOException, ParseException {
        String url = this.url + "/posts/post/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
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
    { "query": { "match": { "content": "검색어" } }, "_source": ["post_seq"], "collapse": {"field" : "post_seq"}, "from": 0, "size": 10 }
     */
    private JSONObject getCommentSearchRequest(String keyword, int offset, int limit, JSONObject query) throws IOException, ParseException {
        String url = this.url + "/comments/comment/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
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

    /*
    GET /posts/post/_search
    {
    "query": {
        "bool": {
            "must": [ {"multi_match": {"query": "검색어","fields": ["title", "content"]  }  }, { "terms": { "subject_seq": [9, 10, 11] } }]
        }
    },
    "_source": ["title", "content"],
    "from": 0,"size": 10 }
     */
    private JSONObject getPostSearchRequestWithSubject(String keyword, int offset, int limit, List<Subject> subjects, JSONObject multiMatch) throws IOException, ParseException {
        String url = this.url + "/posts/post/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray mustArray = new JSONArray();

        // 검색어 조건
        mustArray.add(multiMatch);

        // subject_seq 조건
        JSONArray subjectSeqArray = new JSONArray();
        for (Subject subject : subjects) {
            subjectSeqArray.add(subject.getSubject_seq());
        }
        JSONObject terms = new JSONObject();
        terms.put("subject_seq", subjectSeqArray);
        JSONObject termsQuery = new JSONObject();
        termsQuery.put("terms", terms);
        mustArray.add(termsQuery);

        bool.put("must", mustArray);
        query.put("bool", bool);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);
//        System.out.println("searchRequest = " + searchRequest.toString());
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
    GET /contents/content/_search
    {
    "query": {
        "bool": {
            "must": [ { "match": { "content": "검색어" } }, { "terms": { "subject_seq": [9, 10, 11] } }]
        }
    },
    "_source": ["post_seq"],
    "collapse": {"field" : "post_seq"}
    "from": 0,"size": 10 }
     */
    private JSONObject getCommentSearchRequestWithSubject(String keyword, int offset, int limit, List<Subject> subjects, JSONObject match) throws IOException, ParseException {
        String url = this.url + "/comments/comment/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray mustArray = new JSONArray();

        // 검색어 조건
        mustArray.add(match);

        // subject_seq 조건
        JSONArray subjectSeqArray = new JSONArray();
        for (Subject subject : subjects) {
            subjectSeqArray.add(subject.getSubject_seq());
        }
        JSONObject terms = new JSONObject();
        terms.put("subject_seq", subjectSeqArray);
        JSONObject termsQuery = new JSONObject();
        termsQuery.put("terms", terms);
        mustArray.add(termsQuery);

        bool.put("must", mustArray);
        query.put("bool", bool);
        searchRequest.put("query", query);
        searchRequest.put("_source", Arrays.asList("post_seq"));
        searchRequest.put("collapse", new JSONObject().put("field", "post_seq"));
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
}
