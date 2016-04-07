package se3a04.twentyonequestions.MessagePassing;

/**
 * MessageChannel
 *      Pipeline allowing messages to be passed between the controller classes
 *      Simplifies the problem of having two threads communicate with one another
 */
public class MessageChannel {

    /**
     * Fields
     *      answer: answer to be passed
     *      question: question to be passed
     *      extra: any extra objects that need to be passed between controllers (map, etc)
     *      type: type of question to be passed
     *      Turn: enumeration of the possible actors who can take turns using this channel
     *      canGo: the controller class which can use the channel
     */
    private String answer= "";
    private String question = "";
    private Object extra = null;
    private QuestionType type = QuestionType.REGULAR;



    private enum Turn {GUI, QUESTION};
    private Turn canGo = Turn.QUESTION;

    public MessageChannel(){}

    /**
     * setAnswer
     *      Sets the answer given by the user
     * @param answer: yes/no answer from the user
     */
    public synchronized void setAnswer(String answer){
        this.answer = answer;
        canGo = Turn.QUESTION;
    }

    /**
     * setQuestion
     *      Sets the question given by the controller
     * @param question the question given by the controller
     */
    public synchronized void setQuestion(String question){
        this.question = question;
        canGo = Turn.GUI;
    }

    /**
     * canGetQuestion
     *      states if the gui can get a question
     * @return if a question is ready for the user interface
     */
    public synchronized boolean canGetQuestion(){
        return Turn.GUI == canGo;
    }

    /**
     * canGetAnswer
     *      states if the answer is ready from the user
     * @return if an answer is ready for the controller
     */
    public synchronized boolean canGetAnswer(){
        return Turn.QUESTION == canGo;
    }

    /**
     * getQuestion
     *      gives the question from the controller
     * @return the question from the controller
     */
    public synchronized String getQuestion(){
        return question;
    }

    /**
     * getAnswer
     *      give the answer from the user
     * @return the answer from the user
     */
    public synchronized String getAnswer(){
        return answer;
    }


    /**
     * getType
     *      Returns the type of the question being asked
     * @return question type
     */
    public synchronized QuestionType getType() {
        return type;
    }

    /**
     * setType
     *      sets the type of the question
     * @param type the question type to ask
     */
    public synchronized void setType(QuestionType type) {
        this.type = type;
    }

    /**
     * getExtra
     *      returns the extra object in the channel
     * @return: extra object
     */
    public Object getExtra() {
        return extra;
    }

    /**
     * setExtra
     *      Sets the extra object in the channel
     * @param extra: Extra object to put in the channel
     */
    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
