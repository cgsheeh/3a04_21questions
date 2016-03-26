package se3a04.twentyonequestions.MessagePassing;

/**
 * Created by curtis on 12/03/16.
 */
public class MessageChannel {

    private String answer= "";
    private String question = "";
    private Object extra = null;
    private QuestionType type = QuestionType.REGULAR;



    private enum Turn {GUI, QUESTION};
    private Turn canGo = Turn.QUESTION;

    public MessageChannel(){

    }

    /**
     *Sets the answer given by the user
     * @param answer yes,no or maybe from user
     */
    public synchronized void setAnswer(String answer){
        this.answer = answer;
        canGo = Turn.QUESTION;
    }

    /**
     * Sets the question given by the controller
     * @param question the question given by the controller
     */
    public synchronized void setQuestion(String question){
        this.question = question;
        canGo = Turn.GUI;
    }

    /**
     * states if the gui can get a question
     * @return if a question is ready for the user interface
     */
    public synchronized boolean canGetQuestion(){
        return Turn.GUI == canGo;
    }

    /**
     * states if the answer is ready from the user
     * @return if an answer is ready for the controller
     */
    public synchronized boolean canGetAnswer(){
        return Turn.QUESTION == canGo;
    }

    /**
     * gives the question from the controller
     * @return the question from the controller
     */
    public synchronized String getQuestion(){
        return question;
    }

    /**
     * give the answer from the user
     * @return the answer from the user
     */
    public synchronized String getAnswer(){
        return answer;
    }


    /**
     * Returns the type of the question being asked
     * @return question type
     */
    public synchronized QuestionType getType() {
        return type;
    }

    /**
     * sets the type of the question
     * @param type the question type to ask
     */
    public synchronized void setType(QuestionType type) {
        this.type = type;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
