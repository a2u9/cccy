package pro.antonvmax;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.TimeUnit;

public class Operations {

    public final static long SLEEP_SEC = 3;
    public final static long WAIT_SEC = 1;

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);


        new Thread(new Runnable() {
            public void run() {
                System.out.println("Begin transfer a->b");
                try {
                    transfer(a, b, 500);
                } catch (InsufficientResourcesException e) {
                    e.printStackTrace();
                    System.out.println("InsufficientResourcesException a->b");
                }
                System.out.println("End of transfer a->b");
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println("Begin transfer b->a");
                try {
                    transfer(b, a, 300);
                } catch (InsufficientResourcesException e) {
                    e.printStackTrace();
                    System.out.println("InsufficientResourcesException b->a");
                }
                System.out.println("End of transfer b->a");
            }
        }).start();
    }

    private static void transfer(Account acc1, Account acc2, int amount) throws InsufficientResourcesException {
        try {
            // lock 1
            if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                System.out.println("Locked acc1 in thread " + Thread.currentThread().getName());
                try {
                    //pause
                    System.out.println("Sleep in thread " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(SLEEP_SEC * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Error in sleep");
                    }
                    // lock 2
                    if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        System.out.println("Locked acc2 in thread " + Thread.currentThread().getName());
                        try {
                            if (acc1.getBalance() < amount) {
                                throw new InsufficientResourcesException();
                            }

                            acc1.withdraw(amount);
                            acc2.deposit(amount);

                            System.out.println("Transfer done in thread " + Thread.currentThread().getName());
                        } finally {
                            System.out.println("Unlocked acc2 in thread " + Thread.currentThread().getName());
                            acc2.getLock().unlock();
                        }
                    } else {
                        System.out.println("Can't wait for lock in thread " + Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Error in lock 2");
                } finally {
                    System.out.println("Unlocked acc1 in thread " + Thread.currentThread().getName());
                    acc1.getLock().unlock();
                }
            } else {
                System.out.println("Can't wait for lock in thread " + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error in lock 1");
        }
    }

}
