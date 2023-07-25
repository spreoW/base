/**
 * @author :wangq
 * @date : 2023/7/11 15:28
 */
public class TestVolatile{
    private static boolean flag = false;
    public static void main(String[] args) {
        new Thread(()->{
            try {
                Thread.sleep(1000);
                flag = true;
                System.out.println(Thread.currentThread().getName() + "修改flag字段");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(()->{
            while (true){
                if(flag){
                    System.out.println("-----");
                }
            }
        }).start();
    }

}
