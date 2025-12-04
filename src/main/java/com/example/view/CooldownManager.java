package com.example.view;

public class CooldownManager {
    private boolean onCooldown = false;
    private long cooldownEnd = 0;

    public boolean isReady() {
        return !onCooldown || System.currentTimeMillis() >= cooldownEnd;
    }

    public void startCooldown(int seconds) {
        onCooldown = true;
        cooldownEnd = System.currentTimeMillis() + seconds * 1000;
    }

    public double getCooldownPercent(int seconds) {
        long left = cooldownEnd - System.currentTimeMillis();
        if (left <= 0) return 0;
        return (double) left / (seconds * 1000);
    }

}
