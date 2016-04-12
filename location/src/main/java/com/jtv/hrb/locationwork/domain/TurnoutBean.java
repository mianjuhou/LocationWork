package com.jtv.hrb.locationwork.domain;

import java.util.ArrayList;
import java.util.List;

public class TurnoutBean {
	private ZhuanZheBean zhuanZheBean=new ZhuanZheBean();
	private DaoQuXianBean daoQuXianBean=new DaoQuXianBean();
	private ZheChaBean zheChaBean=new ZheChaBean();
	private List<ZhiJuBean> zhiJuBeans=new ArrayList<ZhiJuBean>();
	public ZhuanZheBean getZhuanZheBean() {
		return zhuanZheBean;
	}
	public void setZhuanZheBean(ZhuanZheBean zhuanZheBean) {
		this.zhuanZheBean = zhuanZheBean;
	}
	public DaoQuXianBean getDaoQuXianBean() {
		return daoQuXianBean;
	}
	public void setDaoQuXianBean(DaoQuXianBean daoQuXianBean) {
		this.daoQuXianBean = daoQuXianBean;
	}
	public ZheChaBean getZheChaBean() {
		return zheChaBean;
	}
	public void setZheChaBean(ZheChaBean zheChaBean) {
		this.zheChaBean = zheChaBean;
	}
	public List<ZhiJuBean> getZhiJuBeans() {
		return zhiJuBeans;
	}
	public void setZhiJuBeans(List<ZhiJuBean> zhiJuBeans) {
		this.zhiJuBeans = zhiJuBeans;
	}
	
}
