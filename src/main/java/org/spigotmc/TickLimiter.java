package org.spigotmc;

import java.util.concurrent.atomic.AtomicBoolean;

public class TickLimiter {

    private final int maxTime;
    private volatile long startTime;
    private final AtomicBoolean triggered = new AtomicBoolean(false);

    public TickLimiter(int maxtime) {
        this.maxTime = maxtime;
    }

    public void initTick() {
        startTime = System.currentTimeMillis();
    }

    public boolean shouldContinue() {
        long remaining = System.currentTimeMillis() - startTime;
        boolean timeNotOuted = remaining < maxTime;
        this.triggered.set(!timeNotOuted);
        return timeNotOuted;
    }

    public boolean triggered(){
        return this.triggered.get();
    }
}
