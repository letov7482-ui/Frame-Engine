package net.yourname.smoothpacing.metrics;

import java.util.ArrayList;
import java.util.List;

public class FrameTimeTracker {
    private static final int MAX_STORAGE = 300; // Храним историю за последние 300 кадров
    private static final List<Long> FRAME_HISTORY = new ArrayList<>(MAX_STORAGE);

    public static synchronized void addFrameTime(long nanoTime) {
        if (FRAME_HISTORY.size() >= MAX_STORAGE) {
            FRAME_HISTORY.remove(0);
        }
        FRAME_HISTORY.add(nanoTime);
    }

    public static synchronized List<Long> getHistory() {
        return new ArrayList<>(FRAME_HISTORY);
    }
}
