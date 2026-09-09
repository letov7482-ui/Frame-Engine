package net.yourname.smoothpacing.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.renderer.GameRenderer;
import net.yourname.smoothpacing.core.ResolutionScaler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient minecraft;
    
    private int originalWidth;
    private int originalHeight;
    private boolean isScaled = false;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void beforeRenderLevel(CallbackInfo ci) {
        ResolutionScaler.tickScaler();
        float scale = ResolutionScaler.getCurrentScale();

        if (scale < 1.0f) {
            RenderTarget mainBuffer = this.minecraft.getMainRenderTarget();
            
            // Запоминаем оригинальный размер экрана
            this.originalWidth = mainBuffer.width;
            this.originalHeight = mainBuffer.height;
            
            // Считаем новое сжатое разрешение для 3D сцены
            int newWidth = ResolutionScaler.scaleWidth(this.originalWidth);
            int newHeight = ResolutionScaler.scaleHeight(this.originalHeight);
            
            // На лету меняем размеры буфера (без полной перезагрузки текстур)
            mainBuffer.width = newWidth;
            mainBuffer.height = newHeight;
            mainBuffer.viewWidth = newWidth;
            mainBuffer.viewHeight = newHeight;
            
            isScaled = true;
        }
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void afterRenderLevel(CallbackInfo ci) {
        if (isScaled) {
            RenderTarget mainBuffer = this.minecraft.getMainRenderTarget();
            
            // Срочно возвращаем оригинальные размеры до начала отрисовки GUI/Худа
            mainBuffer.width = this.originalWidth;
            mainBuffer.height = this.originalHeight;
            mainBuffer.viewWidth = this.originalWidth;
            mainBuffer.viewHeight = this.originalHeight;
            
            isScaled = false;
        }
    }
}
