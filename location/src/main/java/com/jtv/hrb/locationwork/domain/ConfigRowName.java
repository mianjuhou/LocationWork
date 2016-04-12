package com.jtv.hrb.locationwork.domain;

/**
 * Created by fangdean on 2016/3/24.
 */
public class ConfigRowName {

    /**
     * CP1NANUMCFGNUM : 1001
     * CP1NANUMCFGROWID : 51
     * CP1NANUMCFGROWNUM : 1002
     * DESCRIPTION :
     * HASLD : 0
     * LINENUM : 2
     * ORGID : ORG2
     * PARTTYPE :
     * ROWSTAMP : 691410707
     * SITEID : S05118
     */

    private String CP1NANUMCFGNUM;
    private int CP1NANUMCFGROWID;
    private String CP1NANUMCFGROWNUM;
    private String DESCRIPTION;
    private int HASLD;
    private int LINENUM;
    private String ORGID;
    private String PARTTYPE;
    private String ROWSTAMP;
    private String SITEID;

    public ConfigRowName() {
    	
	}
    
	public ConfigRowName(String cP1NANUMCFGROWNUM, String dESCRIPTION) {
		CP1NANUMCFGROWNUM = cP1NANUMCFGROWNUM;
		DESCRIPTION = dESCRIPTION;
	}

	public void setCP1NANUMCFGNUM(String CP1NANUMCFGNUM) {
        this.CP1NANUMCFGNUM = CP1NANUMCFGNUM;
    }

    public void setCP1NANUMCFGROWID(int CP1NANUMCFGROWID) {
        this.CP1NANUMCFGROWID = CP1NANUMCFGROWID;
    }

    public void setCP1NANUMCFGROWNUM(String CP1NANUMCFGROWNUM) {
        this.CP1NANUMCFGROWNUM = CP1NANUMCFGROWNUM;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public void setHASLD(int HASLD) {
        this.HASLD = HASLD;
    }

    public void setLINENUM(int LINENUM) {
        this.LINENUM = LINENUM;
    }

    public void setORGID(String ORGID) {
        this.ORGID = ORGID;
    }

    public void setPARTTYPE(String PARTTYPE) {
        this.PARTTYPE = PARTTYPE;
    }

    public void setROWSTAMP(String ROWSTAMP) {
        this.ROWSTAMP = ROWSTAMP;
    }

    public void setSITEID(String SITEID) {
        this.SITEID = SITEID;
    }

    public String getCP1NANUMCFGNUM() {
        return CP1NANUMCFGNUM;
    }

    public int getCP1NANUMCFGROWID() {
        return CP1NANUMCFGROWID;
    }

    public String getCP1NANUMCFGROWNUM() {
        return CP1NANUMCFGROWNUM;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public int getHASLD() {
        return HASLD;
    }

    public int getLINENUM() {
        return LINENUM;
    }

    public String getORGID() {
        return ORGID;
    }

    public String getPARTTYPE() {
        return PARTTYPE;
    }

    public String getROWSTAMP() {
        return ROWSTAMP;
    }

    public String getSITEID() {
        return SITEID;
    }
}
