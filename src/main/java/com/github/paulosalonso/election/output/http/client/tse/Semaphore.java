package com.github.paulosalonso.election.output.http.client.tse;

import java.util.Timer;
import java.util.TimerTask;

public class Semaphore {

    private boolean closed = false;

    public boolean isClosed() {
        return closed;
    }

    public void close(long closeForMillis) {
        closed = true;
        new Timer().schedule(getTimerTask(), closeForMillis);
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Semaphore.this.closed = false;
            }
        };
    }
}
