import java.util.concurrent.*;

public class TestCase02 {

    //https://www.jianshu.com/p/0b1feed12149
    //https://my.oschina.net/u/221218/blog/213995
    //https://blog.csdn.net/p_programmer/article/details/84679474
    //https://blog.csdn.net/p_programmer/article/details/84677762?utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control
    public static void main(String[] args){

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future f = es.submit(() -> {
            System.out.println("execute call");
            Thread.sleep(8000);
            return 5;
        });
        try {
            System.out.println(f.isDone()); //检测任务是否完成
            System.out.println(f.get(10000, TimeUnit.MILLISECONDS));
            System.out.println(f.isDone()); //检测任务是否完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        es.shutdown();

    }



}
