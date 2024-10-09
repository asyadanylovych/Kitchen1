import java.util.concurrent.Semaphore;

public class Main {
    static final Semaphore cookingSlots = new Semaphore(4); // Максимальна кількість страв, що можуть готуватися одночасно
    private static boolean isOpen = true;

    // Метод для перевірки, чи відкрита кухня
    public static synchronized boolean isOpen() {
        return isOpen;
    }

    // Метод для закриття кухні
    public static synchronized void closeKitchen() {
        isOpen = false;
        System.out.println("============ Кухня закрилась ============");
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable chefRunnable = () -> {
            while (true) {
                // Перевірка, чи кухня відкрита
                if (!isOpen()) {
                    break; // Кухар закінчує роботу, якщо кухня закрита
                }
                try {
                    System.out.printf("%s: починає приготування страви \n", Thread.currentThread().getName());
                    cookingSlots.acquire(); // Чекаємо доступу до плити

                    if (!isOpen()) { // Перевіряємо ще раз, чи кухня відкрита
                        cookingSlots.release(); // Звільняємо слот
                        break;
                    }

                    System.out.printf("%s: починає приготування страви...\n", Thread.currentThread().getName());
                    Thread.sleep(1500); // Імітація часу приготування страви

                    System.out.printf("%s: страва готова!\n", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    System.err.printf("%s: процес перервано \n", Thread.currentThread().getName());
                } finally {
                    cookingSlots.release(); // Звільняємо слот на плиті
                }
            }
        };

        // Створення кухарів та початок їх роботи
        for (int i = 1; i <= 5; i++) {
            Thread chefThread = new Thread(chefRunnable, "Кухар " + i);
            chefThread.start();
        }

        // Імітуємо робочі години кухні
        Thread.sleep(10000); // Чекаємо 10 секунд
        closeKitchen(); // Закриваємо кухню

        Thread.sleep(2000); // Додатковий час для завершення потоків
        System.out.println("============ Всі кухарі завершили роботу ============");
    }
}
