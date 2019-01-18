package com.oleg.givevoice.db.gvquestions;

import java.math.BigInteger;

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

//    /**
//     * Item photo
//     */
//    @com.google.gson.annotations.SerializedName("Photo")
//    private String mPhoto;

//    /**
//     * Indicates if the item deleted
//     */
//    @com.google.gson.annotations.SerializedName("deleted")
//    private boolean mDeleted;

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
     * Indicates if the item is marked as completed
     */
//    public boolean isDeleted() {
//        return mDeleted;
//    }
//    public boolean getDeleted() {
//        return mDeleted;
//    }
//    /**
//     * Marks the item as completed or incompleted
//     */
//    public void setDeleted(boolean deleted) {
//        mDeleted = deleted;
//    }

//    /!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    @Override
    public boolean equals(Object o) {
        return o instanceof GVQuestion && ((GVQuestion) o).mId == mId;
    }
}