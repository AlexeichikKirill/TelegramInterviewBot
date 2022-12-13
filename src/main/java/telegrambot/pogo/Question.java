package telegrambot.pogo;

public class Question {
    private final String question;
    private final String description;

    public Question(String question, String description) {
        this.question = question;
        this.description = description;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getDescription() {
        return this.description;
    }
}
