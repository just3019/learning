import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author demon
 * @version 1.0.0
 */
public class JavaTest {

    private static final String t = "t";
    private static final Demo demo = new Demo();


    public static void main(String[] args) {
        System.out.println(demo.toString());
        demo.setA("a");
        System.out.println(demo.toString());


        float f = 1f;
        for (int i = 0; i < 20; i++) {
            f = f * 1.08f;
        }
        System.out.println(f);

        String a = "a";
        System.out.println(a.hashCode());
        String b = "a";
        System.out.println(b.hashCode());

        Map<TestDemo, String> map = new HashMap<>();
        map.put(new TestDemo("hello"), "helo");
        System.out.println(map.get(new TestDemo("hello")));

    }

    static class TestDemo {
        private String a;

        public TestDemo(String a) {
            this.a = a;
        }
    }


    @Data
    static class Demo {
        private String a = "1";
        private String b = "2";


    }
}
