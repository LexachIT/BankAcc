import java.util.Random;

class Account {
    private double balance = 0.0;

    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Пополнение: " + amount + " Баланс: " + balance);
        notifyAll(); // Уведомляем ожидающие потоки о том, что баланс изменился
    }

    public synchronized void withdraw(double amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Недостаточно средств для снятия " + amount + "  Ожидание.");
            wait(); // Ожидаем, пока баланс не будет достаточным
        }
        balance -= amount;
        System.out.println("Снято: " + amount + " Остаток: " + balance);
    }

    public synchronized double getBalance() {
        return balance;
    }
}

public class Main {
    private static final Random random = new Random();
    private static final Account account = new Account();

    public static void main(String[] args) {
        // Поток для пополнения баланса
        Thread depositThread = new Thread(() -> {
            try {
                while (true) {
                    double amount = random.nextDouble() * 100; // Случайная сумма от 0 до 100
                    account.deposit(amount);
                    Thread.sleep(1000); // Задержка перед следующим пополнением
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        depositThread.start();

        // Вывод остатка и попытка снятия средств
        try {
            // Ожидание, пока баланс не достигнет определенной суммы для снятия
            account.withdraw(150); // Пытались снять 150, если не хватает, ждем
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Закрытие потока после завершения
        depositThread.interrupt();
    }
}
