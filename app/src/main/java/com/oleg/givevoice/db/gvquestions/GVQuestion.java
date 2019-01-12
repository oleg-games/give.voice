package com.oleg.givevoice.db.gvquestions;

/**
 * Represents an item in a ToDo list
 */
public class GVQuestion {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("QuestionText")
    private String mQuestionText;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item photo
     */
    @com.google.gson.annotations.SerializedName("Photo")
    private String mPhoto;

    /**
     * Indicates if the item deleted
     */
    @com.google.gson.annotations.SerializedName("deleted")
    private boolean mDeleted;

    /**
     * GVQuestion constructor
     */
    public GVQuestion() {

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
    public GVQuestion(String text, String id) {
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

    /**
     * Indicates if the item is marked as completed
     */
    public boolean isDeleted() {
        return mDeleted;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

//    /!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    @Override
    public boolean equals(Object o) {
        return o instanceof GVQuestion && ((GVQuestion) o).mId == mId;
    }
}