package com.oleg.givevoice.db.gvquestionsanswers;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Represents an item in a ToDo list
 */
public class GVQuestionAnswer implements Serializable {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("question")
    private String mQuestion;

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String mText;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("questionId")
    private String mQuestionId;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("toPhone")
    private String mToPhone;

    /**
     * GVQuestion constructor
     */
    public GVQuestionAnswer() {

    }

    protected GVQuestionAnswer(Parcel in) {
        mQuestion = in.readString();
        mText = in.readString();
        mId = in.readString();
        mQuestionId = in.readString();
        mToPhone = in.readString();
    }
    
    @Override
    public String toString() {
        return getText();
    }

    /**
     * Initializes a new GVQuestion
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public GVQuestionAnswer(String text, String id) {
        this.setText(text);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setText(String text) {
        mText = text;
    }

    /**
     * Returns the item text
     */
    public String getQuestion() {
        return mQuestion;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setQuestion(String text) {
        mQuestion = text;
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
     * Returns the item id
     */
    public String getQuestionId() {
        return mQuestionId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setQuestionId(String id) {
        mQuestionId = id;
    }

    /**
     * Returns the item id
     */
    public String getToPhone() {
        return mToPhone;
    }

    /**
     * Sets the item id
     *
     * @param phone
     *            id to set
     */
    public final void setToPhone(String phone) {
        mToPhone= phone;
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
}