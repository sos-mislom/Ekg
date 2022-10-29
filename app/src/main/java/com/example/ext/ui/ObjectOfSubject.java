package com.example.ext.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

public class ObjectOfSubject implements Parcelable {
    public String paramOne;
    public Map<String, ArrayList> paramToo;
    public ObjectOfSubject(String paramOne, Map<String, ArrayList> paramToo) {
        this.paramOne = paramOne;
        this.paramToo = paramToo;
    }

    protected ObjectOfSubject(Parcel in) {
        paramOne = in.readString();
    }

    public static final Creator<ObjectOfSubject> CREATOR = new Creator<ObjectOfSubject>() {
        @Override
        public ObjectOfSubject createFromParcel(Parcel in) {
            return new ObjectOfSubject(in);
        }

        @Override
        public ObjectOfSubject[] newArray(int size) {
            return new ObjectOfSubject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(paramOne);
        dest.writeMap(paramToo);
    }
}
