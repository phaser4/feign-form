package net.phaser4.feign;

import net.phaser4.feign.FormEncoderTest.SampleDto;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/sendInt")
    public int sendInt(int intParam) {
        return intParam;
    }

    @PostMapping("/sendDto")
    public SampleDto sendDto(@ModelAttribute SampleDto dto) {
        return dto;
    }
}
