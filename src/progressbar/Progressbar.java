package progressbar;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class Progressbar {
    public final String progressBarString = "#";
    public final String progressBarStartString = "[";
    public final String progressBarEndString = "]";
    public final int barLength = 100;
    DecimalFormat percentageFormat;
    Timer timer;

    String title;
    int numberOfSteps;
    public AtomicInteger currentStepCounter;
    public AtomicInteger currentBarCounter;

    public Progressbar(String title, int numberOfSteps) {
        this.title = title;
        this.numberOfSteps = numberOfSteps;
        currentStepCounter = new AtomicInteger(0);
        this.currentBarCounter = new AtomicInteger(0);
        this.percentageFormat =new DecimalFormat("0.00");
        this.timer=new Timer();
        timer.startTimer();
        System.out.print(getProgressBar());
    }

    public void countUp() {
        int currentStep = currentStepCounter.incrementAndGet();

        double currentFactor = 1.0*numberOfSteps / currentStep;

        int currentBarsShouldBe = (int) (barLength / currentFactor);

        if (currentBarsShouldBe != currentBarCounter.get()) {
            currentBarCounter.set(currentBarsShouldBe);
            System.out.print(getProgressBar());
        }


        if (numberOfSteps==currentStepCounter.get()){
            System.out.println();
            timer.stopTimer();
        }


    }

    private String getProgressBar(){
        StringBuilder sb=new StringBuilder("\r");
        sb.append(this.title);
        sb.append(": ");
        double percentage=this.currentStepCounter.get()*1.0/this.numberOfSteps;
        sb.append(percentageFormat.format(percentage*100)).append("% ");
        sb.append(progressBarStartString);
        sb.append(progressBarString.repeat(Math.max(0, currentBarCounter.get() + 1)));
        sb.append(" ".repeat(Math.max(0, barLength - (currentBarCounter.get() + 1))));
        sb.append(progressBarEndString).append(" time:");
        sb.append((timer.getCurrentTimeInSeconds()));

        return sb.toString();
    }

}



