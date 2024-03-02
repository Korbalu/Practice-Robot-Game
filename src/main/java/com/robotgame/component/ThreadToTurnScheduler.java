package com.robotgame.component;

import com.robotgame.service.CustomUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ThreadToTurnScheduler {

    private CustomUserService customUserService;
    private Thread thread;

    public ThreadToTurnScheduler(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @PostConstruct
    public void start() {
        thread = new Thread(new TurnTask());
        thread.start();
    }

    @PreDestroy
    public void stop() {
        thread.interrupt();
    }

    private class TurnTask implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                customUserService.giveTurnsToUsers();
                try {
                    Thread.sleep(120000); // 2*60*1000 - min
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}


