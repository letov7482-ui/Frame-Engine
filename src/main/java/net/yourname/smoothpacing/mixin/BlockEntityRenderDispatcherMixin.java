package net.yourname.smoothpacing.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.yourname.smoothpacing.core.FrameBudgetManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderBlockEntity(BlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, CallbackInfo ci) {
        // Агрессивное отсечение рендера некритичных Block Entity на расстоянии, 
        // если текущий кадр превышает лимит герцовки экрана
        if (FrameBudgetManager.getTargetFrameTimeMs() < 2.0) { 
            double distanceSq = blockEntity.getBlockPos().distToCenterSqr(
                net.minecraft.client.MinecraftClient.getInstance().player.position()
            );
            
            // Если объект дальше 32 блоков и фреймтайм плохой — не рендерим его в этом кадре
            if (distanceSq > 1024) { 
                ci.cancel();
            }
        }
    }
}
