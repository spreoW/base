import java.math.BigDecimal;

/**
 * @author :wangq
 * @date : 2023/7/10 17:20
 */
public class Test {
    private static Object a;
    public static void main(String[] args) {
        int j = 0;
        for (int i = 0; i < 100; i++) {
            System.out.println(j);
            System.out.println(2*j++);
            System.out.println(j);
        }
        System.out.println(j);
    }

    private static void aa(){
        synchronized (a){

        }
    }
}
