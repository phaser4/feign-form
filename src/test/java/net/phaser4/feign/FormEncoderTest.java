package net.phaser4.feign;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FormEncoderTest.TestContext.class)
public class FormEncoderTest {

    @Autowired
    private TestClient testClient;

    @Test
    public void testSendingInt() {
        int value = 3;
        int returned = testClient.sendInt(value);

        assertThat(returned).isEqualTo(value);
    }

    @Test
    public void testSendingDto() {
        val dto = new SampleDto(1, ImmutableList.of("test"));
        val returned = testClient.sendDto(dto);

        assertThat(returned).isEqualTo(dto);
    }

    @FeignClient(name = "test", configuration = TestClientConfiguration.class)
    interface TestClient {
        @RequestMapping(method = POST, value = "/sendInt")
        int sendInt(@RequestParam("intParam") int intParam);

        @RequestMapping(method = POST, value = "/sendDto", consumes = APPLICATION_FORM_URLENCODED_VALUE)
        SampleDto sendDto(@ModelAttribute SampleDto dto);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class SampleDto {
        public int intValue;
        public List<String> stringValues;
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients
    static class TestContext {
        @Bean
        public TestController testController() { return new TestController(); }
    }
}
