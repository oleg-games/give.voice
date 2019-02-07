package com.oleg.givevoice.db.gvquestionsanswers;

import com.oleg.givevoice.db.gvanswers.GVAnswer;

import java.io.Serializable;
import java.util.Objects;

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
    @com.google.gson.annotations.SerializedName("questionImage")
    private String mQuestionImage;

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
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("fromPhone")
    private String mFromPhone;

    /**
     * Item image
     */
    @com.google.gson.annotations.SerializedName("image")
    private String mImage;

    /**
     * GVQuestion constructor
     */
    public GVQuestionAnswer() {

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
     * Sets the item id
     *
     * @param phone
     *            id to set
     */
    public final void setToPhone(String phone) {
        mToPhone= phone;
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
    public final void setFromPhone(String phone) {
        mFromPhone= phone;
    }

    /**
     * Returns the item id
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Sets the item id
     *
     * @param image
     *            id to set
     */
    public final void setImage(String image) {
        mImage = image;
    }

    /**
     * Returns the item id
     */
    public String getQuestionImage() {
        return mQuestionImage;
    }

    /**
     * Sets the item id
     *
     * @param image
     *            id to set
     */
    public final void setQuestionImage(String image) {
        mQuestionImage = image;
    }

    /**
     * Returns the item id
     */
    public String getFromPhone() {
        return mFromPhone;
    }

    @Override
    public String toString() {
        return "GVQuestionAnswer{" +
                "mQuestion='" + mQuestion + '\'' +
                ", mQuestionImage='" + mQuestionImage + '\'' +
                ", mText='" + mText + '\'' +
                ", mId='" + mId + '\'' +
                ", mQuestionId='" + mQuestionId + '\'' +
                ", mToPhone='" + mToPhone + '\'' +
                ", mFromPhone='" + mFromPhone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVQuestionAnswer that = (GVQuestionAnswer) o;
        return Objects.equals(mQuestion, that.mQuestion) &&
                Objects.equals(mQuestionImage, that.mQuestionImage) &&
                Objects.equals(mText, that.mText) &&
                Objects.equals(mId, that.mId) &&
                Objects.equals(mQuestionId, that.mQuestionId) &&
                Objects.equals(mToPhone, that.mToPhone) &&
                Objects.equals(mFromPhone, that.mFromPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mQuestion, mQuestionImage, mText, mId, mQuestionId, mToPhone, mFromPhone);
    }

    public GVAnswer getAnswer() {
        return new GVAnswer(getId(), getText(), getToPhone(), getQuestionId());
    }
}