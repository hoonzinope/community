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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Value("${spring.elasticsearch.uris}")
    private String url;

    private final PostDAO postDAO;

    private final SubjectDAO subjectDAO;

    private final UserDAO userDAO;

    @Autowired
    public SearchService(PostDAO postDAO, SubjectDAO subjectDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.subjectDAO = subjectDAO;
        this.userDAO = userDAO;
    }

    public JSONObject search(
            String keyword,
            int offset, int limit,
            String type) {
        JSONObject result = new JSONObject();
        JSONObject searchResult = new JSONObject();
        try {
            List<Long> diableble_subject_list =
                    subjectDAO.getSubjectListUseN()
                            .stream()
                            .map(subject -> subject.getSubject_seq())
                            .collect(Collectors.toList());
            if(type.equalsIgnoreCase("comment")) {
                searchResult = getCommentSearch(keyword, offset, limit, diableble_subject_list);
            }else{
                searchResult = getPostSearch(keyword, offset, limit, diableble_subject_list);
            }
            List<Long> postSeqList = getPostSeqList(searchResult);
            result.put("searchResult", searchResult);
            if(type.equalsIgnoreCase("comment")) {
                result.put("post_comment_list", getPostCommentList(postDAO.getPostListByPostSeqList(postSeqList), searchResult));
            }else{
                result.put("post_list", postDAO.getPostListByPostSeqList(postSeqList));
            }
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

    private JSONArray getPostCommentList(List<PostPagingDTO> posts, JSONObject commentSearchResult) {
        JSONArray postCommentList = new JSONArray();

        if(commentSearchResult.get("hits") != null) {
            JSONObject hits = (JSONObject) commentSearchResult.get("hits");
            if (hits.get("hits") != null) {
                JSONArray hitsArray = (JSONArray) hits.get("hits");
                for (Object o : hitsArray) {
                    JSONObject hit = (JSONObject) o;
                    JSONObject source = (JSONObject) hit.get("_source");
                    if(source != null) {
                        Long postSeq = (Long) source.get("post_seq");
                        PostPagingDTO post = posts.stream().filter(postPagingDTO -> postPagingDTO.getPost_seq() == postSeq).findFirst().get();

                        // source json에 post 정보추가
                        String user_nickname = userDAO.getUserBySeq((long) source.get("user_seq")).getUser_nickname();
                        source.put("user_nickname", user_nickname);
                        source.put("post", post);
                        postCommentList.add(source);
                    }
                }
            }
        }

        return postCommentList;
    }

    /*
    GET /posts/post/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "multi_match": {
                "query":  "test",
                "fields": ["title", "content"]
              }
            }
          ],
          "must_not": [
            {
              "terms": { "subject_seq": [5] }
            }
          ],
          "filter": [
            { "term": { "delete_flag": false } }
          ]
        }
      }
    }
     */
    private JSONObject getPostSearch(String keyword, int offset, int limit, List<Long> disable_subject_list) throws IOException, ParseException {
        String url = this.url + "/posts/_doc/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray mustArray = new JSONArray();

        // 검색어 조건
        JSONObject multiMatch = new JSONObject();
        multiMatch.put("query", keyword);
        multiMatch.put("fields", Arrays.asList("title", "content"));
        JSONObject multiMatchQuery = new JSONObject();
        multiMatchQuery.put("multi_match", multiMatch);
        mustArray.add(multiMatchQuery);

        // 제외 조건
        JSONObject mustNotTerms = new JSONObject();
        JSONObject subject_seq_list = new JSONObject();
        subject_seq_list.put("subject_seq", disable_subject_list);
        mustNotTerms.put("terms", subject_seq_list);
        JSONArray mustNotArray = new JSONArray();
        mustNotArray.add(mustNotTerms);

        // 필터 조건
        JSONObject filterTerm = new JSONObject();
        JSONObject delete_flag = new JSONObject();
        delete_flag.put("delete_flag", false);
        filterTerm.put("term", delete_flag);
        JSONArray filterArray = new JSONArray();
        filterArray.add(filterTerm);

        bool.put("must", mustArray);
        bool.put("must_not", mustNotArray);
        bool.put("filter", filterArray);
        query.put("bool", bool);
        searchRequest.put("query", query);
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/json으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));
//        System.out.println(searchRequest.toString());
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

    /*
    GET /comments/comment/_search
    {
      "query": {
        "bool": {
          "must": [
            { "match": { "content": "test" } }
          ],
          "must_not": [
            {
              "terms": {
                "subject_seq": [2, 5, 7]
              }
            }
          ],
          "filter": [
            { "term": { "delete_flag": false } }
          ]
        }
      }
    }
     */
    private JSONObject getCommentSearch(String keyword, int offset, int limit, List<Long> disable_subject_list) throws IOException, ParseException {

        String url = this.url + "/comments/_doc/_search";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject searchRequest = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray mustArray = new JSONArray();
        JSONArray mustNotArray = new JSONArray();
        JSONArray filterArray = new JSONArray();

        // 검색어 조건
        JSONObject match = new JSONObject();
        match.put("content", keyword);
        JSONObject matchQuery = new JSONObject();
        matchQuery.put("match", match);
        mustArray.add(matchQuery);

        // 제외 조건
        JSONObject mustNotTerms = new JSONObject();
        JSONObject subject_seq_list = new JSONObject();
        subject_seq_list.put("subject_seq", disable_subject_list);
        mustNotTerms.put("terms", subject_seq_list);
        mustNotArray.add(mustNotTerms);

        // 필터 조건
        JSONObject filterTerm = new JSONObject();
        JSONObject delete_flag = new JSONObject();
        delete_flag.put("delete_flag", false);
        filterTerm.put("term", delete_flag);
        filterArray.add(filterTerm);

        bool.put("must", mustArray);
        bool.put("must_not", mustNotArray);
        bool.put("filter", filterArray);
        query.put("bool", bool);
        searchRequest.put("query", query);
        searchRequest.put("from", offset);
        searchRequest.put("size", limit);

        // 헤더에 Content-Type을 application/json으로 설정
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(searchRequest.toString(), "UTF-8"));
//        System.out.println(searchRequest.toString());
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

}
