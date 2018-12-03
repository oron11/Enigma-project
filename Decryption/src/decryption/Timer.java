package decryption;

import java.util.concurrent.TimeUnit;

public class Timer {
    /*public static void main(String[] args) {
        Timer timer = new Timer();
        int num = getFromUserNumber()
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) { }

        System.out.println(timer.getTimeTillNow());

        timer.pauseTimer();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) { }

        System.out.println(timer.getTimeTillNow());
        timer.continueTimer();
        System.out.println(timer.getTimeTillNow());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) { }

        System.out.println(timer.getTimeTillNow());

    }*/

    private long time;
    private long seconds;
    private long minutes;
    private boolean isPaused;

    public Timer() {
        time = System.nanoTime();
        seconds = 0;
        minutes = 0;
        isPaused = false;
    }

    public void pauseTimer() {
        isPaused = true;
        holdTimer();
    }

    private void holdTimer() {
        long elapsedTime = System.nanoTime() - time;
        int secondsToAdd = (int)(TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS)) % 60;
        int minutesToAdd = (int) TimeUnit.MINUTES.convert(elapsedTime, TimeUnit.NANOSECONDS);

        minutes += minutesToAdd;
        if(secondsToAdd + seconds >= 60) {
            minutes++;
            seconds = seconds + secondsToAdd - 60;
        }
        else {
            seconds = seconds + secondsToAdd;
        }

    }

    public void continueTimer() {
        isPaused = false;
        time = System.nanoTime();
    }

    public String getTimeTillNow() {
        if(!isPaused) {
            holdTimer();
            time = System.nanoTime();
        }

        return String.format("%02d:%02d", minutes, seconds);
    }
}
