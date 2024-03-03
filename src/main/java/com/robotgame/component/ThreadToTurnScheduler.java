package com.robotgame.component;

import com.robotgame.domain.CustomUser;
import com.robotgame.repository.CustomUserRepository;
import com.robotgame.service.CustomUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ThreadToTurnScheduler {

    private CustomUserRepository customUserRepository;
    private Thread thread;

    public ThreadToTurnScheduler(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
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
                int minutesForTurnsToGive = 5;
                List<CustomUser> users = customUserRepository.findAll();

                for (CustomUser user : users) {
                    LocalDateTime lastTurnGiven = user.getLastTimeTurnGiven();
                    long minutesSinceLastTurn = Duration.between(lastTurnGiven, LocalDateTime.now()).toMinutes();

                    int turnsToAdd = ((int) minutesSinceLastTurn / minutesForTurnsToGive);

                    if (turnsToAdd > 0) {
                        int currentTurns = user.getTurns() == null ? 0 : user.getTurns();
                        user.setTurns(Math.min((currentTurns + turnsToAdd), 250));
//                user.setLastTimeTurnGiven(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)); //this one glitches once, then works in time
                        user.setLastTimeTurnGiven((user.getLastTimeTurnGiven().plus(turnsToAdd * minutesForTurnsToGive, ChronoUnit.MINUTES))
                                .truncatedTo(java.time.temporal.ChronoUnit.SECONDS));

                        customUserRepository.save(user);
                    }
                }
                try {
                    Thread.sleep(60000); // 1*60*1000 - min
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}


