package net.phaser4.feign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class BeanToMapConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> convert(Object bean) {
        return convertBean(bean)
                .collect(toMap(KeyValue::getKey, KeyValue::getValue));
    }

    private Stream<KeyValue> convertBean(Object bean) {
        if (bean == null)
            return Stream.empty();

        val stream = convertViaJackson(bean);
        return stream.flatMap(this::remap);
    }

    private Stream<KeyValue> remap(KeyValueRaw kv) {
        if (kv.value == null)
            return Stream.empty();

        if (isList(kv)) {
            return processList(kv);
        }

        if (isNumber(kv) || isString(kv)) {
            return processSimpleObject(kv);
        }

        return processObject(kv);
    }

    private Stream<KeyValue> processSimpleObject(KeyValueRaw kv) {
        return Stream.of(KeyValue.of(kv.key, kv.value.toString()));
    }

    private Stream<KeyValue> processObject(KeyValueRaw kv) {
        val stream = convertBean(kv.value);
        return stream.map(k -> KeyValue.of(kv.key + "." + k.key, k.value));
    }

    private boolean isNumber(KeyValueRaw kv) {
        return Number.class.isAssignableFrom(kv.value.getClass());
    }

    private boolean isString(KeyValueRaw kv) {
        return String.class.isAssignableFrom(kv.value.getClass());
    }

    private boolean isList(KeyValueRaw kv) {
        return List.class.isAssignableFrom(kv.value.getClass());
    }

    private Stream<KeyValue> processList(KeyValueRaw kv) {
        List list = (List)kv.value;
        return IntStream.range(0, list.size())
                .mapToObj(index -> KeyValueRaw.of(kv.key + "[" + index + "]", list.get(index)))
                .flatMap(this::remap);
    }

    private Stream<KeyValueRaw> convertViaJackson(Object bean) {
        Map<String, Object> map = objectMapper.convertValue(bean, new TypeReference<Map<String, Object>>() {});
        return map.keySet().stream()
                .map(key -> KeyValueRaw.of(key, map.get(key)));
    }

    @Value
    @AllArgsConstructor(staticName = "of")
    private static class KeyValue {
        public final String key, value;
    }

    @Value
    @AllArgsConstructor(staticName = "of")
    private static class KeyValueRaw {
        public final String key;
        public final Object value;
    }
}
