package com.njucm;

import com.njucm.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WpSearchService.class)
public class ClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testCategory() {
        List<String> list = categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        list.forEach(System.out::println);
    }
}
