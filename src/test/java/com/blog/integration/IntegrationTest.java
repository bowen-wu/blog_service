package com.blog.integration;

import com.blog.Application;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class IntegrationTest {
    @Inject
    Environment environment;

    @Test
    public void notLoggedInByDefault() {
        String port = environment.getProperty("local.server.port");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:" + port + "/auth");
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println(response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                Assertions.assertEquals(200, response.getStatusLine().getStatusCode());
                Assertions.assertTrue(json.contains("isLogin\":false"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
