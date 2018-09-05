package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class FaqItem {
    private String question;
    private String answer;
    private boolean isPressed = false;

    public FaqItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }
}
