package com.jtv.hrb.locationwork.domain;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by fangdean on 2016/3/15.
 */
public class CurveLine /*implements Parcelable*/ {
    //曲线起始位置
    private double startmeasure;
    //曲线结束位置
    private double endmeasure;
    //曲线描述
    private String label;
    //曲线全长
    private double xxqc;
    //终端缓和曲线
    private int zhxc;
    //起端缓和曲线
    private int qhxc;
    //曲线半径
    private int qxbj;
    //曲线id
    private int assetfeatureid;
    //计划正矢
    private List<Double> planvalue;


	public int getAssetfeatureid() {
		return assetfeatureid;
	}

	public void setAssetfeatureid(int assetfeatureid) {
		this.assetfeatureid = assetfeatureid;
	}

	public double getStartmeasure() {
        return startmeasure;
    }

    public void setStartmeasure(double startmeasure) {
        this.startmeasure = startmeasure;
    }

    public double getEndmeasure() {
        return endmeasure;
    }

    public void setEndmeasure(double endmeasure) {
        this.endmeasure = endmeasure;
    }


    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getXxqc() {
        return xxqc;
    }

    public void setXxqc(double xxqc) {
        this.xxqc = xxqc;
    }

    public int getZhxc() {
        return zhxc;
    }

    public void setZhxc(int zhxc) {
        this.zhxc = zhxc;
    }

    public int getQhxc() {
        return qhxc;
    }

    public void setQhxc(int qhxc) {
        this.qhxc = qhxc;
    }

    public int getQxbj() {
        return qxbj;
    }

    public void setQxbj(int qxbj) {
        this.qxbj = qxbj;
    }

    public List<Double> getPlanvalue() {
        return planvalue;
    }

    public void setPlanvalue(List<Double> planvalue) {
        this.planvalue = planvalue;
    }


    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.startmeasure);
        dest.writeDouble(this.endmeasure);
        dest.writeString(this.labe);
        dest.writeDouble(this.xxqc);
        dest.writeInt(this.zhxc);
        dest.writeInt(this.qhxc);
        dest.writeInt(this.qxbj);
        dest.writeList(this.planvalue);
    }

    public CurveLine() {
    }

    protected CurveLine(Parcel in) {
        this.startmeasure = in.readDouble();
        this.endmeasure = in.readDouble();
        this.labe = in.readString();
        this.xxqc = in.readDouble();
        this.zhxc = in.readInt();
        this.qhxc = in.readInt();
        this.qxbj = in.readInt();
        this.planvalue = new ArrayList<Double>();
        in.readList(this.planvalue, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<CurveLine> CREATOR = new Parcelable.Creator<CurveLine>() {
        public CurveLine createFromParcel(Parcel source) {
            return new CurveLine(source);
        }

        public CurveLine[] newArray(int size) {
            return new CurveLine[size];
        }
    };*/
}
