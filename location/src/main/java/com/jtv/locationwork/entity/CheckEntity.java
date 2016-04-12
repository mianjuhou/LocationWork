package com.jtv.locationwork.entity;

import com.jtv.dbentity.annotation.Column;
import com.jtv.dbentity.annotation.Id;
import com.jtv.dbentity.annotation.Table;

/**
 * 线路检查的实体bean对象
 * 
 * @author Administrator
 *
 */
@Table(name = "checkquestion")
public class CheckEntity {
	@Column(name = "lineSelector")
	private String lineSelector;// 线路或者道岔
	@Column(name = "line")
	private String line;// 线
	@Column(name = "type")
	private String type;// 行
	@Column(name = "rule")
	private String rule;// 尺
	@Column(name = "cha")
	private String cha;// 道岔部分
	@Column(name = "tie")
	private String tie;// 铁
	@Column(name = "measure")
	private String measure;// 测量
	@Column(name = "km")
	private String km;// 千米
	@Column(name = "question")
	private String question;// 问题
	@Column(name = "questiontype")
	private String questiontype;// 问题类型
	@Column(name = "description")
	private String description;// 描述
	@Column(name = "path")
	private String path;// 图片路径
	@Column(name = "audiopath")
	private String audiopath;// 音频路径

	@Column(name = "other")
	private String other;// 无

	@Column(name = "statu")
	private int statu;// 无
	
	@Column(name = "time")
	private long time;//保存时间
	
	@Column(name = "wonum")
	private String wonum;//工单

	@Id
	@Column(name = "check_id")
	private int check_id;// 代表是同一个问题

	public String getLineSelector() {
		return lineSelector;
	}

	public void setLineSelector(String lineSelector) {
		this.lineSelector = lineSelector;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getCha() {
		return cha;
	}

	public void setCha(String cha) {
		this.cha = cha;
	}

	public String getTie() {
		return tie;
	}

	public void setTie(String tie) {
		this.tie = tie;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestiontype() {
		return questiontype;
	}

	public void setQuestiontype(String questiontype) {
		this.questiontype = questiontype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAudiopath() {
		return audiopath;
	}

	public void setAudiopath(String audiopath) {
		this.audiopath = audiopath;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public int getCheck_id() {
		return check_id;
	}

	public void setCheck_id(int check_id) {
		this.check_id = check_id;
	}

	public int getStatu() {
		return statu;
	}

	public void setStatu(int statu) {
		this.statu = statu;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getWonum() {
		return wonum;
	}

	public void setWonum(String wonum) {
		this.wonum = wonum;
	}

}
