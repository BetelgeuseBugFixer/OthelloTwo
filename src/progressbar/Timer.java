package progressbar;

import java.util.ArrayList;

public class Timer {
    private long currentTime;
    private long timeStart;
    private boolean isRunning;
    private ArrayList<Long> timeStops;

    public Timer() {
        this.currentTime = 0;
        this.timeStart = 0;
        isRunning = false;
        this.timeStops = new ArrayList<>();
    }

    public void startTimer() {
        if (!isRunning) {
            this.timeStart = System.currentTimeMillis();
            isRunning = true;
        } else {
            System.err.println("timer is already running, you can not start a new one");
        }
    }

    public void stopTimer(boolean addToStopTime) {
        if (isRunning) {
            this.currentTime += System.currentTimeMillis() - this.timeStart;
            if (addToStopTime) {
                this.timeStops.add(currentTime);
            }
            isRunning = false;
        } else {
            System.err.println("timer is not running, you can not stop it");
        }
    }

    public void stopTimer() {
        stopTimer(true);
    }

    public long getCurrentTimeInMilliSeconds() {
        return this.currentTime;
    }

    public double getCurrentTimeInSeconds() {
        if (isRunning) {
            return (System.currentTimeMillis() - this.timeStart) / (1000 * 1.0);
        } else {
            return this.currentTime / (1000 * 1.0);
        }
    }

    public ArrayList<Long> getTimeStops() {
        return timeStops;
    }
}
