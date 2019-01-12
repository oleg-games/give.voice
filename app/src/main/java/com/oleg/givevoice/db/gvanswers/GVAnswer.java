package com.oleg.givevoice.db.gvanswers;

/**
 * Represents an item in a ToDo list
 */
public class GVAnswer {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("AnswerText")
    private String mQuestionText;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

//    /**
//     * Indicates if the item is completed
//     */
//    @com.google.gson.annotations.SerializedName("complete")
//    private boolean mComplete;

    /**
     * GVQuestion constructor
     */
    public GVAnswer() {

    }

    @Override
    public String toString() {
        return getQuestionText();
    }

    /**
     * Initializes a new GVQuestion
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public GVAnswer(String text, String id) {
        this.setQuestionText(text);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getQuestionText() {
        return mQuestionText;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setQuestionText(String text) {
        mQuestionText = text;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }

//    /**
//     * Indicates if the item is marked as completed
//     */
//    public boolean isComplete() {
//        return mComplete;
//    }

//    /**
//     * Marks the item as completed or incompleted
//     */
//    public void setComplete(boolean complete) {
//        mComplete = complete;
//    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GVAnswer && ((GVAnswer) o).mId == mId;
    }
}