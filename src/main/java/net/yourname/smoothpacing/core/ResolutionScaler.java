package net.yourname.smoothpacing.core;

public class ResolutionScaler {
    private static float currentScale = 1.0f;
    private static final float MIN_SCALE = 0.5f; // Минимальный порог сжатия (50% разрешения)
    private static final float STEP_DOWN = 0.05f; // На сколько сжимать при фризах (по 5%)
    private static final float STEP_UP = 0.01f;   // Как плавно восстанавливать разрешение

    public static void tickScaler() {
        if (FrameBudgetManager.checkAndResetOverload()) {
            // Если кадр не уложился в герцовку, снижаем разрешение рендера мира
            currentScale = Math.max(MIN_SCALE, currentScale - STEP_DOWN);
        } else if (currentScale < 1.0f) {
            // Если все плавно, очень плавно возвращаем к 100%
            currentScale = Math.min(1.0f, currentScale + STEP_UP);
        }
    }

    public static float getCurrentScale() {
        return currentScale;
    }
    
    public static int scaleWidth(int originalWidth) {
        return Math.max(1, (int) (originalWidth * currentScale));
    }

    public static int scaleHeight(int originalHeight) {
        return Math.max(1, (int) (originalHeight * currentScale));
    }
}
