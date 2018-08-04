package pro.antonvmax;

import javax.naming.InsufficientResourcesException;

public class Operations {

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);


        new Thread(new Runnable() {
            public void run() {
                try {
                    transfer(a, b, 500);
                } catch (InsufficientResourcesException e) {
                    e.printStackTrace();
                    System.out.println("InsufficientResourcesException a->b");
                }
                System.out.println("Successful transfer a->b");
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    transfer(b, a, 300);
                } catch (InsufficientResourcesException e) {
                    e.printStackTrace();
                    System.out.println("InsufficientResourcesException b->a");
                }
                System.out.println("Successful transfer b->a");
            }
        }).start();
    }

    private static void transfer(Account acc1, Account acc2, int amount) throws InsufficientResourcesException {
        if (acc1.getBalance() < amount) {
            throw new InsufficientResourcesException();
        }
        acc1.withdraw(amount);
        acc2.deposit(amount);
    }

}
