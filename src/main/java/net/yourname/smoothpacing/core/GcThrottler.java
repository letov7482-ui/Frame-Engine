package net.yourname.smoothpacing.core;

public class GcThrottler {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static long lastGcCheckTime = 0;
    private static boolean isGcImpending = false;

    public static boolean shouldThrottle() {
        long currentTime = System.currentTimeMillis();
        // Проверяем память раз в 500 мс, чтобы сам замер не грузил процессор
        if (currentTime - lastGcCheckTime > 500) {
            lastGcCheckTime = currentTime;
            long maxMemory = RUNTIME.maxMemory();
            long allocatedMemory = RUNTIME.totalMemory();
            long freeMemory = RUNTIME.freeMemory();
            long usedMemory = allocatedMemory - freeMemory;

            // Если занято более 85% выделенной Java-памяти, GC скоро активируется
            isGcImpending = (double) usedMemory / maxMemory > 0.85;
        }
        
        // Тормозим потоки чанков, если память забита ИЛИ если фреймтайм уже пробивает лимит герцовки
        return isGcImpending || FrameBudgetManager.getTargetFrameTimeMs() < 1.0; 
    }
}
