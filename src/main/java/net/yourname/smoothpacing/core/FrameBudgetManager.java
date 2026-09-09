package net.yourname.smoothpacing.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Window;

public class FrameBudgetManager {
    private static double targetFrameTimeMs = 16.66;
    private static int currentRefreshRate = 60;
    private static boolean needsResolutionScaleDown = false;

    public static void updateBudget(MinecraftClient client) {
        Window window = client.getWindow();
        // В 1.21.11 метод getRefreshRate() возвращает герцовку монитора из GLFW
        int refreshRate = window.getRefreshRate();
        
        if (refreshRate > 0 && refreshRate != currentRefreshRate) {
            currentRefreshRate = refreshRate;
            // Считаем лимит времени на 1 кадр (например, для 144 Гц = 6.94 мс)
            targetFrameTimeMs = 1000.0 / refreshRate;
        }
    }

    public static double getTargetFrameTimeMs() {
        return targetFrameTimeMs;
    }

    public static long getTargetFrameTimeNano() {
        return (long) (targetFrameTimeMs * 1_000_000);
    }

    public static void signalFrameOverload() {
        needsResolutionScaleDown = true;
    }

    public static boolean checkAndResetOverload() {
        boolean status = needsResolutionScaleDown;
        needsResolutionScaleDown = false;
        return status;
    }
}
