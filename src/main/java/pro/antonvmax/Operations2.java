package pro.antonvmax;

import java.util.concurrent.*;


public class Operations2 {

    private static final ExecutorService service = Executors.newFixedThreadPool(3);

//    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws InterruptedException {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        /*final Runnable reporter = new Runnable() {
            public void run() {
                System.out.println("a.fails " + a.getFailCounter());
                System.out.println("b.fails " + b.getFailCounter());
            }
        };
        final ScheduledFuture<?> serviceHandle = scheduler.scheduleAtFixedRate(
                reporter, 1, 1, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                serviceHandle.cancel(true);
            }
        },60, TimeUnit.SECONDS);*/

        for (int lcv = 0; lcv < 10; lcv++) {
            service.submit(new Transfer(lcv, a, b, 1000));
        }
        service.shutdown();
        service.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("a.fails " + a.getFailCounter());
        System.out.println("b.fails " + b.getFailCounter());
    }
}
