package net.yourname.smoothpacing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.mojang.blaze3d.platform.InputConstants;

public class SmoothPacingClient implements ClientModInitializer {
    private boolean isKeyAlreadyPressed = false;

    @Override
    public void onInitializeClient() {
        // Слушаем конец каждого тика клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() != null) {
                // Код GLFW для Right Shift — 344
                boolean isPressed = InputConstants.isKeyDown(client.getWindow().getHandle(), 344);
                
                if (isPressed && !isKeyAlreadyPressed) {
                    isKeyAlreadyPressed = true;
                    
                    // Переключаем экран: если открыт наш GUI — закрываем его, если нет — открываем
                    if (client.screen instanceof SmoothGuiScreen) {
                        client.setScreen(null);
                    } else if (client.screen == null) {
                        client.setScreen(new SmoothGuiScreen());
                    }
                } else if (!isPressed) {
                    isKeyAlreadyPressed = false; // Сбрасываем триггер, чтобы нажатие не спамило
                }
            }
        });
    }
}
