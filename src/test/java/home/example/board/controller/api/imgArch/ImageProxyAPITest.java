package home.example.board.controller.api.imgArch;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class ImageProxyAPITest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() {
        String testKey = "test:image";
        String testVal = UUID.randomUUID().toString() + ".jpg";
        stringRedisTemplate.opsForValue().set(testKey, testVal, 60, TimeUnit.SECONDS);
        String val = stringRedisTemplate.opsForValue().get(testKey);
        System.out.println("val = [" + val + "]");
    }

}