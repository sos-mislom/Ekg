package com.example.ext.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class ObjectOfDiary implements Parcelable {
    public String paramOne;
    public List<String> paramToo;
    public ObjectOfDiary(String paramOne, List<String> paramToo) {
        this.paramOne = paramOne;
        this.paramToo = paramToo;
    }

    protected ObjectOfDiary(Parcel in) {
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
        dest.writeArray(new List[]{paramToo});
    }
}
