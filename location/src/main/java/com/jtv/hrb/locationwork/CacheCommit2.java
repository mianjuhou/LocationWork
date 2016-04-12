package com.jtv.hrb.locationwork;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.alibaba.fastjson.util.Base64;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.MainMeasureData;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.hrb.locationwork.domain.TableLevelOne;
import com.jtv.locationwork.util.Base64UtilCst;

public class CacheCommit2 {
	private Context mContext;
	private StaticCheckDbService service;
	public CacheCommit2(Context context) {
		mContext=context;
		service=new StaticCheckDbService(mContext);
	}
	
	public void setSyncState(int state){
		boolean syncResult = service.setSyncState(state);
	}
	
	public String getCommitJson(){
		try {
			JSONArray arrayOne=new JSONArray();
			JSONArray arrayTwo=new JSONArray();
			JSONArray arrayThree=new JSONArray();
			//获取一级表数据
			ArrayList<TableLevelOne> tableLevelOnes = getTableLevelOnes();
			if(isEmpty(tableLevelOnes)){
				return null;
			}
			for (TableLevelOne tableLevelOne : tableLevelOnes) {
				//转为JSON
				arrayOne.put(getTableLevelOneJO(tableLevelOne));
				//获取直线的二级表数据
				ArrayList<MainMeasureData> tableLevelTwoInStraightList = getTableLevelTwoInStraightByWonum(tableLevelOne.getWonum());
				if(!isEmpty(tableLevelTwoInStraightList)){
					for (MainMeasureData mainMeasureData : tableLevelTwoInStraightList) {
						//转为JSON
						arrayTwo.put(getTableLevelTwoJO(mainMeasureData));
						//获取直线的三级表数据
						ArrayList<MeasureData> tableLevelThreeInStraightList = getTableLevelThreeInStraight(mainMeasureData);
						if(!isEmpty(tableLevelThreeInStraightList)){
							for (MeasureData measureData : tableLevelThreeInStraightList) {
								//转为JSON
								arrayThree.put(getTableLevelThreeJO(measureData));
							}
						}
					}
				}
				
				//获取曲线的二级表数据
				ArrayList<MainMeasureData> tableLevelTwoInCurveList = getTableLevelTwoInCurveByWonum(tableLevelOne.getWonum());
				if(!isEmpty(tableLevelTwoInCurveList)){
					for (MainMeasureData mainMeasureData : tableLevelTwoInCurveList) {
						//转为JSON
						arrayTwo.put(getTableLevelTwoJO(mainMeasureData));
						//获取曲线的三级表数据
						ArrayList<MeasureData> tableLevelThreeInCurveList = getTableLevelThreeInCurve(mainMeasureData);
						if(!isEmpty(tableLevelThreeInCurveList)){
							for (MeasureData measureData : tableLevelThreeInCurveList) {
								//转为JSON
								arrayThree.put(getTableLevelThreeJO(measureData));						
							}
						}
					}
				}
				
				//获取道岔的二级表数据
				ArrayList<MainMeasureData> tableLevelTwoInTurnoutList = getTableLevelTwoInTurnoutByWonum(tableLevelOne.getWonum());
				if(!isEmpty(tableLevelTwoInTurnoutList)){
					for (MainMeasureData mainMeasureData : tableLevelTwoInTurnoutList) {
						//转为JSON
						arrayTwo.put(getTableLevelTwoJO(mainMeasureData));
						//获取道岔的三级表数据
						ArrayList<MeasureData> tableLevelThreeInStraightList = getTableLevelThreeInTurnout(mainMeasureData);
						if(!isEmpty(tableLevelThreeInStraightList)){
							for (MeasureData measureData : tableLevelThreeInStraightList) {
								//转为JSON
								arrayThree.put(getTableLevelThreeJO(measureData));
							}
						}
					}
				}
			}
			JSONObject jsonOne=new JSONObject();
			jsonOne.put("tablename", "C_Insp1rawdata");
			jsonOne.put("data", arrayOne);
			
			JSONObject jsonTwo=new JSONObject();
			jsonTwo.put("tablename", "Cp1raw_Zxmain");
			jsonTwo.put("data", arrayTwo);
			
			JSONObject jsonThree=new JSONObject();
			jsonThree.put("tablename", "Cp1raw_Zxmain_R1");
			jsonThree.put("data", arrayThree);
			
			JSONArray topArray=new JSONArray();
			topArray.put(jsonOne);
			topArray.put(jsonTwo);
			topArray.put(jsonThree);
			
			return topArray.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean isEmpty(List list){
		if(list!=null){
			if(list.size()>0){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
	
	private JSONObject getTableLevelThreeJO(MeasureData measureData) {
		try {
			JSONObject jo=new JSONObject();
			jo.put("siteid", measureData.getSiteid());
			jo.put("orgid", measureData.getOrgid());
			jo.put("gh", measureData.getGh());
			jo.put("startmeasure", measureData.getStartmeasure());
			jo.put("endmeasure", measureData.getEndmeasure());
			
			int linenum=measureData.getLinenum();
			if(measureData.getMeasuretype()==4){
				linenum=(measureData.getLinenum()+1)/2;
			}
			jo.put("linenum", linenum);
			
			jo.put("value1", measureData.getValue1());
			jo.put("value2", measureData.getValue2());
			jo.put("hasld",measureData.getHasld());
			jo.put("assetattrid", measureData.getAssetattrid());
			jo.put("assetnum", measureData.getAssetnum());
			jo.put("assetfeatureid", measureData.getAssetfeatureid());
			jo.put("wonum", measureData.getWonum());
			
			jo.put("nametype", Base64UtilCst.encodeUrl(measureData.getNametype()));
			jo.put("status", measureData.getStatus());
			jo.put("defectclass", measureData.getDefectclass());
			jo.put("fvalue", measureData.getFvalue());
			jo.put("flag_v", measureData.getFlag_v());
			if(measureData.getXvalue().length()<=30){
				jo.put("xvalue", measureData.getXvalue());
			}
			if(measureData.getYvalue().length()<=30){
				jo.put("yvalue", measureData.getYvalue());
			}
			jo.put("inspdate", measureData.getInspdate());
			jo.put("cp1nanumcfgrownum", measureData.getCp1nanumcfgrownum());
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getTableLevelTwoJO(MainMeasureData mainMeasureData) {
		try {
			JSONObject jo = new JSONObject();
			jo.put("strartlc", mainMeasureData.getStrartlc());
			jo.put("endlc", mainMeasureData.getEndlc());
			jo.put("gh", mainMeasureData.getGh());
			jo.put("siteid", mainMeasureData.getSiteid());
			jo.put("orgid", mainMeasureData.getOrgid());
			jo.put("wonum", mainMeasureData.getWonum());
			jo.put("assetnum", mainMeasureData.getAssetnum());
			jo.put("hasld",mainMeasureData.getHasld());
			jo.put("assetfeatureid", mainMeasureData.getAssetfeatureid());
			jo.put("zx_zhd_value1", mainMeasureData.getZx_zhd_value1());
			jo.put("zx_zhd_value2", mainMeasureData.getZx_zhd_value2());
			jo.put("zx_hyd_value1", mainMeasureData.getZx_hyd_value1());
			jo.put("zx_hyd_value2", mainMeasureData.getZx_hyd_value2());
			jo.put("zx_yhd_value1", mainMeasureData.getZx_yhd_value1());
			jo.put("zx_yhd_value2", mainMeasureData.getZx_yhd_value1());
			jo.put("zx_hzd_value1", mainMeasureData.getZx_hzd_value1());
			jo.put("zx_hzd_value2", mainMeasureData.getZx_hzd_value1());
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getTableLevelOneJO(TableLevelOne tableLevelOne) {
		try {
			JSONObject jo = new JSONObject();
			jo.put("enterby", GlobalApplication.mBase64Lead);
			jo.put("hasld", tableLevelOne.getHasld());
			jo.put("orgid", tableLevelOne.getOrgid());
			jo.put("siteid", tableLevelOne.getSiteid());
			jo.put("wonum", tableLevelOne.getWonum());
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<TableLevelOne> getTableLevelOnes() {
		ArrayList<TableLevelOne> tableLevelOnes=service.getAllTableLevelOne();
		if(!isEmpty(tableLevelOnes)){
			for (TableLevelOne tableLevelOne : tableLevelOnes) {
				tableLevelOne.setEnterby(GlobalApplication.mBase64Lead);
			}
		}
		return tableLevelOnes;
	}
	private ArrayList<MainMeasureData> getTableLevelTwoInStraightByWonum(String wonum){
		return service.getTableLevelTwoInStraightByWonum(wonum);
	}
	
	private ArrayList<MainMeasureData> getTableLevelTwoInCurveByWonum(String wonum){
		return service.getTableLevelTwoInCurveByWonum(wonum);
	}
	
	private ArrayList<MainMeasureData> getTableLevelTwoInTurnoutByWonum(String wonum){
		ArrayList<MainMeasureData> mainMeasureDatas=service.getTableLevelTwoInTurnoutByWonum(wonum);
		if(mainMeasureDatas!=null&&mainMeasureDatas.size()>0){
			for (MainMeasureData mainMeasureData : mainMeasureDatas) {
				mainMeasureData.setOrgid(GlobalApplication.orgid);
				mainMeasureData.setSiteid(GlobalApplication.siteid);
			}
		}
		return mainMeasureDatas;
	}
	
	private ArrayList<MeasureData> getTableLevelThreeInStraight(MainMeasureData mainMeasureData){
		return service.getTableLevelThreeInStraight(mainMeasureData);
	}
	
	private ArrayList<MeasureData> getTableLevelThreeInCurve(MainMeasureData mainMeasureData){
		return service.getTableLevelThreeInCurve(mainMeasureData);
	}
	private ArrayList<MeasureData> getTableLevelThreeInTurnout(MainMeasureData mainMeasureData){
		return service.getTableLevelThreeInTurnout(mainMeasureData);
	}
}
