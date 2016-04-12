package com.jtv.locationwork.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemWonum implements Parcelable {

	// 一个工单中很多属性
	protected HashMap<String, ItemWoListAttribute> item = new LinkedHashMap<String, ItemWoListAttribute>();

	public HashMap<String, ItemWoListAttribute> get(){
		return item;
	}
	
	public void addAttribute(ItemWoListAttribute attr) {
		item.put(attr.getColn(), attr);
	}

	public ItemWoListAttribute getAttribute(String colu) {
		return item.get(colu);
	}
	
	public ItemWonum() {
	}

	public ItemWonum(Parcel in) {
		in.readMap(item, ItemWoListAttribute.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public String toString() {
		Set<String> keySet = item.keySet();
		JSONObject jsonObject = new JSONObject();
		for (String string : keySet) {
			
			ItemWoListAttribute itemWoListAttribute = item.get(string);
			String disValue = itemWoListAttribute.getDisValue();
			try {
				jsonObject.put(string, disValue);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jsonObject.toString();
	}

	@Override
	public int hashCode() {

		String str = "h";
		try {

			Set<String> keySet = item.keySet();
			
			int i =0;
			
			for (String key : keySet) {
				
				ItemWoListAttribute itemWoListAttribute = item.get(key);
				String item = itemWoListAttribute.getDisPlayname() + itemWoListAttribute.getDisValue()+itemWoListAttribute.getColn();
				
				str += item;
				
				i++;
				
				if(i>8){
					break;
				}
				
			}
			
			keySet=null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return str.hashCode();
	}

	@Override
	public boolean equals(Object o) {

		ItemWonum item = (ItemWonum) o;
		int hashCode = item.hashCode();
		int hashCode2 = this.hashCode();

		if (hashCode == hashCode2) {
			return true;
		}

		return super.equals(o);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(item);
	}

	public static final Parcelable.Creator<ItemWonum> CREATOR = new Parcelable.Creator<ItemWonum>() {
		public ItemWonum createFromParcel(Parcel in) {
			return new ItemWonum(in);
		}

		public ItemWonum[] newArray(int size) {
			return new ItemWonum[size];
		}
	};

}
