package pro.antonvmax;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Operations2 {

    private static ExecutorService service = Executors.newFixedThreadPool(3);

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);
        for (int lcv = 0; lcv < 10; lcv++) {
            service.submit(new Transfer(a, b, (int) (1000 * Math.random())));
        }
        service.shutdown();
    }
}
