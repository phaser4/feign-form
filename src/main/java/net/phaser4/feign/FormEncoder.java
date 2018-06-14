package net.phaser4.feign;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class FormEncoder implements Encoder {
    private final BeanToMapConverter beanToMapConverter = new BeanToMapConverter();

    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        val parametersMap = beanToMapConverter.convert(object);
        val charset = resolveCharset(template);
        val body = generateBody(parametersMap, charset);
        template.body(body);
    }

    private String generateBody(Map<String, String> parametersMap, Charset charset) {
        return parametersMap.entrySet().stream()
                .map(e -> urlEncode(e.getKey(), e.getValue(), charset))
                .collect(joining("&"));
    }

    private Charset resolveCharset(RequestTemplate template) {
        Charset charset = template.charset();
        if (charset == null)
            charset = StandardCharsets.UTF_8;
        return charset;
    }

    @SneakyThrows
    private String urlEncode(String parameter, String value, Charset charset) {
        String charsetName = charset.name();
        return URLEncoder.encode(parameter, charsetName) + "=" + URLEncoder.encode(value, charsetName);
    }
}
