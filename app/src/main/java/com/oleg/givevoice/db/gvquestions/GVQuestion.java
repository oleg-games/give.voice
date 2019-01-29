package com.oleg.givevoice.db.gvquestions;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents an item in a ToDo list
 */
public class GVQuestion {

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
     * Item image
     */
    @com.google.gson.annotations.SerializedName("image")
    private String mImage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVQuestion that = (GVQuestion) o;
        return Objects.equals(mText, that.mText) &&
                Objects.equals(mId, that.mId) &&
                Objects.equals(mImage, that.mImage) &&
                Objects.equals(mUserId, that.mUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mText, mId, mImage, mUserId);
    }

    /**
     * Indicates if the item deleted
     */
    @com.google.gson.annotations.SerializedName("userId")
    private BigInteger mUserId;

    /**
     * GVQuestion constructor
     */
    public GVQuestion() {

    }

    @Override
    public String toString() {
        return "GVQuestion{" +
                "mText='" + mText + '\'' +
                ", mId='" + mId + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mUserId=" + mUserId +
                '}';
    }

    /**
     * Initializes a new GVQuestion
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public GVQuestion(String text, BigInteger userId, String id) {
        this.setText(text);
        this.setUserId(userId);
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
    public BigInteger getUserId() {
        return mUserId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setUserId(BigInteger id) {
        mUserId = id;
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
}