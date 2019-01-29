package com.oleg.givevoice.db.gvverificationuser;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "GVVerificationUser{" +
                "mUserPhone='" + mUserPhone + '\'' +
                ", mId='" + mId + '\'' +
                ", mCode='" + mCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVVerificationUser that = (GVVerificationUser) o;
        return Objects.equals(mUserPhone, that.mUserPhone) &&
                Objects.equals(mId, that.mId) &&
                Objects.equals(mCode, that.mCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUserPhone, mId, mCode);
    }
}