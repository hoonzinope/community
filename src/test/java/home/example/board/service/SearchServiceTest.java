package home.example.board.service;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void searchTest() {
        String keyword = "테스트";
        String searchType = "all";
        int offset = 0;
        int limit = 10;

        // Call the search method
        JSONObject result = searchService.search(keyword, offset, limit, searchType);

        System.out.println("Search Result: " + result.toJSONString());
    }


    @Test
    public void searchTest2() {
        String keyword = "테스트";
        String searchType = "all";
        int offset = 0;
        int limit = 10;
        long subject_seq = 29;

        // Call the search method
        JSONObject result = searchService.search(keyword, offset, limit, searchType, subject_seq);

        System.out.println("Search Result: " + result.toJSONString());
    }

    @Test
    public void searchTest3() {
        String keyword = "테스트";
        String searchType = "comment";
        int offset = 0;
        int limit = 10;
        long subject_seq = 1;

        // Call the search method
        JSONObject result = searchService.search(keyword, offset, limit, searchType, subject_seq);

        System.out.println("Search Result: " + result.toJSONString());
    }
}