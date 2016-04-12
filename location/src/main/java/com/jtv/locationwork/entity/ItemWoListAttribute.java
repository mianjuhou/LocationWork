package com.jtv.locationwork.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemWoListAttribute implements Parcelable {
	String coln;

	String datatype;

	String disValue;

	String disPlayname;

	String flag;

	String field;

	public void setColn(String coln) {
		this.coln = coln;
	}

	public String getColn() {
		return coln;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public ItemWoListAttribute() {
	}

	public ItemWoListAttribute(Parcel in) {
		datatype = in.readString();
		disValue = in.readString();
		disPlayname = in.readString();
		flag = in.readString();
		field = in.readString();
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDisPlayname() {
		return disPlayname;
	}

	public void setDisPlayname(String disPlayname) {
		this.disPlayname = disPlayname;
	}

	public String getDisValue() {
		return disValue;
	}

	public void setDisValue(String disValue) {
		this.disValue = disValue;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ItemWoListAttribute> CREATOR = new Parcelable.Creator<ItemWoListAttribute>() {
		public ItemWoListAttribute createFromParcel(Parcel in) {
			return new ItemWoListAttribute(in);
		}

		public ItemWoListAttribute[] newArray(int size) {
			return new ItemWoListAttribute[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(datatype);
		dest.writeString(disValue);
		dest.writeString(disPlayname);
		dest.writeString(flag);
		dest.writeString(field);
	}

}
