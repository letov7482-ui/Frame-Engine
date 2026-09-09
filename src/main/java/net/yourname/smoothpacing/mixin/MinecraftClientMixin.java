package net.yourname.smoothpacing.mixin;

import net.minecraft.client.MinecraftClient;
import net.yourname.smoothpacing.core.FrameBudgetManager;
import net.yourname.smoothpacing.metrics.FrameTimeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private long frameStartTime;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(boolean tick, CallbackInfo ci) {
        frameStartTime = System.nanoTime();
        FrameBudgetManager.updateBudget((MinecraftClient) (Object) this);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderEnd(boolean tick, CallbackInfo ci) {
        long frameEndTime = System.nanoTime();
        long durationNano = frameEndTime - frameStartTime;
        
        // Записываем чистый показатель в трекер
        FrameTimeTracker.addFrameTime(durationNano);
        
        // Если вышли за рамки герцовки (например, >6.94 мс при 144 Гц), шлем сигнал перегрузки
        if (durationNano > FrameBudgetManager.getTargetFrameTimeNano()) {
            FrameBudgetManager.signalFrameOverload();
        }
    }
}
