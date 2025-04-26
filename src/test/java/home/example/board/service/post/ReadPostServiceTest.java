package home.example.board.service.post;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class ReadPostServiceTest {

    @Autowired
    ReadPostService readPostService;

    @Test
    void getPostListPaging() {
        // given
        int offset = 0;
        int limit = 10;
        long subject_seq = 1L;

        // when
        JSONObject result = readPostService.getPostListPaging(offset, limit, subject_seq);
        System.out.println(result.toJSONString());
        // then
        assertNotNull(result);
        assertTrue(result.containsKey("postList"));
        assertTrue(result.containsKey("total"));
    }
}