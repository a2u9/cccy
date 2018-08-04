package pro.antonvmax;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Transfer implements Callable<Boolean> {

    public final static long SLEEP_SEC = 1;
    public final static long WAIT_SEC = 1;

    private int id;

    private Account accountFrom, accountTo;
    private int amount;

    public Transfer(int id, Account accountFrom, Account accountTo, int amount) {
        this.id = id;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Boolean call() throws Exception {
        boolean isOk = false;
        try {
            // lock 1
            if (accountFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                System.out.println(id + " Locked acc1 in thread " + Thread.currentThread().getName());
                try {
                    //pause
                    System.out.println(id + " Sleep in thread " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(SLEEP_SEC * 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(id + " Error in sleep");
                    }
                    // lock 2
                    if (accountTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        System.out.println(id + " Locked acc2 in thread " + Thread.currentThread().getName());
                        try {
                            System.out.println(id + " acc2.getBalance() " + accountFrom.getBalance() + " in thread " + Thread.currentThread().getName());
                            if (accountFrom.getBalance() < amount) {
                                throw new InsufficientResourcesException();
                            }

                            accountFrom.withdraw(amount);
                            accountTo.deposit(amount);

                            isOk = true;

                            System.out.println(id + " Transfer done in thread " + Thread.currentThread().getName());
                        } finally {
                            System.out.println(id + " Unlocked acc2 in thread " + Thread.currentThread().getName());
                            accountTo.getLock().unlock();
                        }
                    } else {
                        System.out.println(id + " Can't wait for lock 2 in thread " + Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    accountTo.incFailedTransferCount();
                    System.out.println(id + " Error in lock 2");
                } finally {
                    System.out.println(id + " Unlocked acc1 in thread " + Thread.currentThread().getName());
                    accountFrom.getLock().unlock();
                }
            } else {
                System.out.println(id + " Can't wait for lock 1 in thread " + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            accountFrom.incFailedTransferCount();
            System.out.println(id + " Error in lock 1");
        }
        return isOk;
    }
}
