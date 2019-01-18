package com.oleg.givevoice.db.gvverificationuser;

/**
 * Represents an item in a ToDo list
 */
public class GVVerificationUser{

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("userPhone")
    private String mUserPhone;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("userCode")
    private String mCode;

    /**
     * GVQuestion constructor
     */
    public GVVerificationUser() {

    }

//    @Override
//    public String toString() {
//        return getQuestionText();
//    }

    /**
     * Initializes a new GVQuestion
     *
     * @param mUserPhone
     *            The item text
     * @param id
     *            The item id
     */
    public GVVerificationUser(String mUserPhone, String id) {
        this.setUserPhone(mUserPhone);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getUserPhone() {
        return mUserPhone;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setUserPhone(String text) {
        mUserPhone= text;
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
        return o instanceof GVVerificationUser && ((GVVerificationUser) o).mId == mId;
    }
}