/**
 * @author :wangq
 * @date : 2023/7/19 17:17
 * 测试ThreadLocal内存泄露的场景
 */
public class TestThreadLocal {

    static ThreadLocal tl2 = new ThreadLocal();

    public static void main(String[] args) {
        Aa a = new Aa();
        a.testThreadLocal();
        Thread thread = Thread.currentThread();
        System.gc();
        System.out.println("");
    }

}
class Aa {
    public void testThreadLocal(){
        ThreadLocal tl1 = new ThreadLocal();
        tl1.set("123");
        System.out.println(tl1.get());
    }

}
