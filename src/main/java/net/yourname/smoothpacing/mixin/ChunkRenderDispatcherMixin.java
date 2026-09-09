package net.yourname.smoothpacing.mixin;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.yourname.smoothpacing.core.FrameBudgetManager;
import net.yourname.smoothpacing.core.GcThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.class)
public class ChunkRenderDispatcherMixin {

    @Inject(method = "runTask", at = @At("HEAD"), cancellable = true)
    private void onRunTask(CallbackInfoReturnable<Boolean> cir) {
        // Если фреймтайм нестабилен или память на исходе — пропускаем тяжелую задачу 
        // сборки чанка на этом кадре, перенося её на следующий.
        if (GcThrottler.shouldThrottle()) {
            // Возвращаем false, сигнализируя игре, что свободного бюджета времени в этом кадре нет
            cir.setReturnValue(false);
        }
    }
}
