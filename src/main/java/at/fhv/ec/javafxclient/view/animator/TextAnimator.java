package at.fhv.ec.javafxclient.view.animator;

public class TextAnimator implements Runnable{

    private int initialWaitingTime;
    private int animationTime;
    private String fixedTextElement;
    private String[] dynamicTextElements;
    private TextOutput textOutput;
    private boolean endless;

    public TextAnimator(int initialWaitingTime, int animationTime, String fixedTextElement, String[] dynamicTextElements, TextOutput textOutput, boolean endless) {
        this.initialWaitingTime = initialWaitingTime;
        this.animationTime = animationTime;
        this.fixedTextElement = fixedTextElement;
        this.dynamicTextElements = dynamicTextElements;
        this.textOutput = textOutput;
        this.endless = endless;
    }

    @Override
    public void run() {

        boolean firstRun = false;
        textOutput.writeText(fixedTextElement);

        try {
            Thread.sleep(initialWaitingTime);

            while (!firstRun || endless) {

                for (int i = 0; i < dynamicTextElements.length; i++) {


                    for (int j = 0; j <= dynamicTextElements[i].length(); j++) {
                        String textAtThisPoint = dynamicTextElements[i].substring(0,j);
                        textOutput.writeText(fixedTextElement + textAtThisPoint);
                        Thread.sleep(animationTime);
                    }

                    Thread.sleep(3000);

                    for (int j = dynamicTextElements[i].length(); j >= 0; j--) {
                        String textAtThisPoint = dynamicTextElements[i].substring(0,j);
                        textOutput.writeText(fixedTextElement + textAtThisPoint);
                        Thread.sleep(animationTime);
                    }

                    Thread.sleep(750);

                }

                firstRun = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
