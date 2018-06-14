package net.phaser4.feign;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Value;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class BeanToMapConverterTest {

    @Test
    public void shouldConvertSimpleBeans() {
        val bean = new Object() {
            @Getter private final String sampleString = "hello";
            @Getter private final int sampleInt = 222;
        };

        val map = new BeanToMapConverter().convert(bean);

        assertThat(map.get("sampleString")).isEqualTo("hello");
        assertThat(map.get("sampleInt")).isEqualTo("222");
    }

    @Test
    public void shouldConvertBeansWithLists() {
        val bean = new Object() {
            @Getter private final List<String> list1 = ImmutableList.of("A", "B");
            @Getter private final List<Integer> list2 = ImmutableList.of(1, 2);
        };

        val map = new BeanToMapConverter().convert(bean);

        assertThat(map.get("list1[0]")).isEqualTo("A");
        assertThat(map.get("list2[1]")).isEqualTo("2");
    }

    @Test
    public void shouldConvertBeansWithObjects() {
        val bean = new Object() {
            @Getter public final Inner inner = new Inner();

            @Value
            class Inner {
                public final int field1 = 10;
                public final String field2 = "A";
            }
        };

        val map = new BeanToMapConverter().convert(bean);

        assertThat(map.get("inner.field1")).isEqualTo("10");
        assertThat(map.get("inner.field2")).isEqualTo("A");
    }

    @Test
    public void shouldIgnoreNulls() {
        val map = new BeanToMapConverter().convert(null);

        assertThat(map).isEmpty();
    }

    @Test
    public void shouldIgnoreNullsInFields() {
        val bean = new Object() {
            @Getter private final String value = null;
        };

        val map = new BeanToMapConverter().convert(bean);
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldIgnoreNullsInLists() {
        val bean = new Object() {
            @Getter public final List<String> value = singletonList(null);
        };

        val map = new BeanToMapConverter().convert(bean);
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldConvertRecursively() {
        val bean = new Object() {
            @Getter private final List<Inner> list = ImmutableList.of(new Inner());

            @Value
            class Inner {
                private final List<Innermost> inner = ImmutableList.of(new Innermost());
            }

            @Value
            class Innermost {
                private final String value = "Z";
            }
        };

        val map = new BeanToMapConverter().convert(bean);

        assertThat(map.get("list[0].inner[0].value")).isEqualTo("Z");
    }
}