package com.oleg.givevoice.db.gvuser;

import java.util.Objects;

/**
 * Represents an item in a ToDo list
 */
public class GVUser {

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
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * UserItem constructor
     */
    public GVUser() {

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

    @Override
    public String toString() {
        return "GVUser{" +
                "mText='" + mText + '\'' +
                ", mId='" + mId + '\'' +
                ", mComplete=" + mComplete +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVUser gvUser = (GVUser) o;
        return mComplete == gvUser.mComplete &&
                Objects.equals(mText, gvUser.mText) &&
                Objects.equals(mId, gvUser.mId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mText, mId, mComplete);
    }
}