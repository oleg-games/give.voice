package com.oleg.givevoice.db.gvverificationcode;

/**
 * Represents an item in a ToDo list
 */
public class GVVerificationCode{

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("phoneNumber")
    private String mPhoneNumner;

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("code")
    private String mCode;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * GVQuestion constructor
     */
    public GVVerificationCode() {

    }

//    @Override
//    public String toString() {
//        return getQuestionText();
//    }

    /**
     * Initializes a new GVQuestion
     *
     * @param mPhoneNumner
     *            The item text
     * @param id
     *            The item id
     */
    public GVVerificationCode(String mPhoneNumner, String id) {
        this.setPhoneNumner(mPhoneNumner);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getPhoneNumner() {
        return mPhoneNumner;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setPhoneNumner(String text) {
        mPhoneNumner= text;
    }

    /**
     * Returns the item text
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setCode(String text) {
        mCode= text;
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
//    public boolean isDeleted() {
//        return mDeleted;
//    }
//
//    /**
//     * Marks the item as completed or incompleted
//     */
//    public void setDeleted(boolean deleted) {
//        mDeleted = deleted;
//    }

//    /!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    @Override
    public boolean equals(Object o) {
        return o instanceof GVVerificationCode && ((GVVerificationCode) o).mId == mId;
    }
}