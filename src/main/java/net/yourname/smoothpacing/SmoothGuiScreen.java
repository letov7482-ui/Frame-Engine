package net.yourname.smoothpacing;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.yourname.smoothpacing.core.FrameBudgetManager;
import net.yourname.smoothpacing.core.ResolutionScaler;
import net.yourname.smoothpacing.metrics.FrameTimeTracker;
import java.util.List;

public class SmoothGuiScreen extends Screen {
    
    public SmoothGuiScreen() {
        super(Component.literal("SmoothPacing Engine"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Рисуем красивый полупрозрачный футуристичный фон
        guiGraphics.fill(15, 15, this.width - 15, this.height - 15, 0xD50A0A10);
        
        // Вспомогательные переменные для разметки текста
        int textX = 30;
        int textY = 30;
        int fontColor = 0x00FFCC; // Неоновый бирюзовый

        // 2. Выводим Текстовую Информацию
        guiGraphics.drawString(this.font, "✦ SMOOTH PACING ENGINE v1.0 ✦", textX, textY, fontColor, true);
        
        int hz = this.minecraft.getWindow().getRefreshRate();
        double targetMs = FrameBudgetManager.getTargetFrameTimeMs();
        float currentScale = ResolutionScaler.getCurrentScale();
        
        guiGraphics.drawString(this.font, String.format("Монитор: %d Гц", hz), textX, textY + 20, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, String.format("Целевой Frame Time: %.2f мс", targetMs), textX, textY + 32, 0xAAAAAA, false);
        guiGraphics.drawString(this.font, String.format("Масштаб 3D рендера: %.0f%%", currentScale * 100), textX, textY + 44, currentScale < 1.0f ? 0xFFAA00 : 0x55FF55, false);

        // 3. Строим Честный График Frame Time
        List<Long> history = FrameTimeTracker.getHistory();
        if (history.size() > 1) {
            int graphX = 30;
            int graphY = this.height - 40;
            int graphHeight = 120;
            int graphWidth = this.width - 60;
            
            // Максимальная планка шкалы графика (в миллисекундах) — например, 25 мс
            double maxGraphMs = 25.0; 

            // Рисуем зеленую линию-лимит вашей герцовки
            int targetLineY = graphY - (int)((targetMs / maxGraphMs) * graphHeight);
            if (targetLineY > graphY - graphHeight && targetLineY < graphY) {
                guiGraphics.fill(graphX, targetLineY, graphX + graphWidth, targetLineY + 1, 0x6600FF00);
                guiGraphics.drawString(this.font, "Лимит герцовки", graphX + graphWidth - 100, targetLineY - 10, 0x00FF00, false);
            }

            // Рисуем точки графика
            for (int i = 0; i < history.size() - 1; i++) {
                double msCurrent = history.get(i) / 1_000_000.0;
                double msNext = history.get(i + 1) / 1_000_000.0;
                
                // Рассчитываем шаг по оси X динамически, чтобы график растягивался под экран телефона/ПК
                float stepX = (float) graphWidth / history.size();
                int x1 = graphX + (int)(i * stepX);
                int x2 = graphX + (int)((i + 1) * stepX);
                
                // Проекция миллисекунд на высоту графика Y
                int y1 = graphY - (int)((msCurrent / maxGraphMs) * graphHeight);
                int y2 = graphY - (int)((msNext / maxGraphMs) * graphHeight);
                
                // Защита от выхода за границы сетки
                y1 = Math.max(graphY - graphHeight, Math.min(graphY, y1));
                y2 = Math.max(graphY - graphHeight, Math.min(graphY, y2));

                // Если время кадра превышает лимит герцовки — пиксель становится огненно-красным (зафиксирован статтер)
                int color = (msCurrent > targetMs) ? 0xFF3333 : 0x00FFCC;
                
                // Отрисовываем вертикальные сегменты графика
                guiGraphics.fill(x1, y1, x2, y1 + 2, 0xFF000000 | color);
            }
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Мониторинг должен идти на живой игре, без паузы мира!
    }
}
