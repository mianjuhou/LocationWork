package com.jtv.locationwork.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;

import android.util.Log;

public class CalculateNearWorkListUtils {

	private static String TAG = "CalculateNearWorkListUtils";

	private static SimpleDateFormat sim;

	private static Date date2;

	private static long currTime;

	private static String nearTimeWonum = "";

	/**
	 * 一天的时间毫秒值
	 * 
	 * @return
	 */
	private static long oneDayTime() {
		return 1 * 24 * 60 * 60 * 1000;
	}

	/**
	 * 一小时的时间的毫秒值
	 * 
	 * @return
	 */
	private static long oneHourTime() {
		return 1 * 60 * 60 * 1000;
	}

	/**
	 * 时间的格式是2015-2-22 10:30
	 * 
	 * @param str
	 * @return
	 */
	public static long getTime(String nri, String str) {

		nri = DateUtil.convretyyMMdd(nri);
		str = DateUtil.convretHHmm(str);

		if (sim == null) {
			sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}

		Date date = null;

		try {
			date = sim.parse(nri + " " + str);
		} catch (ParseException e) {
			Log.e(TAG, "日期格式不正确" + e.getMessage());
			return 0;
		}

		long times = date.getTime();

		return times;
	}

	/**
	 * 计算当前的最近单子
	 * 
	 * @param workListArray
	 *            当前的单子集合
	 */
	public static String calculateWonum(List<ItemWonum> workListArray) {

		date2 = new Date();
		currTime = date2.getTime();

		SpUtiles.NearWorkListInfo.deleteYesterday();// 删除昨天的工单

		// 判断最近的登记人员是否和当前的xml人员一致，不一致代表被切换了，需要删除最近工单

		if (TextUtil.isEmpty(GlobalApplication.lead)) {
			SpUtiles.NearWorkListInfo.getSp().edit().clear().commit();
		}

		if (workListArray == null || workListArray.size() < 1) {
			SpUtiles.NearWorkListInfo.getSp().edit().clear().commit();
			return "";
		}

		// 获取文件夹的hashCode
		if (workListArray.size() == 1) {// 只有一个单子，不用算了就是您

			ItemWonum work = workListArray.get(0);

			HashMap<String, ItemWoListAttribute> field = work.get();

			boolean containsKey = field.containsKey(Constants.START_TIME);

			String startTimes = "";

			if (containsKey) {
				ItemWoListAttribute itemWoListAttribute = field.get(Constants.START_TIME);
				startTimes = itemWoListAttribute.getDisValue();
			}

			nearTimeWonum = startTimes;

			SpUtiles.NearWorkListInfo.setAll(work);// 设置为最近工单

			return nearTimeWonum;

		} else {// 代表单子有很多,通过时间来判断当前的单子该属于是谁

			ItemWonum work = null;

			long mins = Long.MAX_VALUE;

			HashMap<String, ItemWoListAttribute> field = null;

			for (int i = 0; i < workListArray.size(); i++) {

				ItemWonum wli = workListArray.get(i);

				field = wli.get();

				String startTimes = "";
				String endTimes = "";

				boolean containsKey = field.containsKey(Constants.START_TIME);

				if (containsKey) {
					ItemWoListAttribute itemWoListAttribute = field.get(Constants.START_TIME);
					startTimes = itemWoListAttribute.getDisValue();
				}

				containsKey = field.containsKey(Constants.END_TIME);

				if (containsKey) {
					ItemWoListAttribute itemWoListAttribute = field.get(Constants.END_TIME);
					endTimes = itemWoListAttribute.getDisValue();
				}

				long min = 0;

				if (!TextUtil.isEmpty(startTimes) && !TextUtil.isEmpty(endTimes)) {
					String[] start = new String[2];
					String[] end = new String[2];
					
					try {
						start = DateUtil.convertyyMMddhhss(startTimes);// 转换为正确的是开始时间
						end = DateUtil.convertyyMMddhhss(endTimes);
					} catch (Exception e) {
					}

					// 防止没有年月日
					if (start == null || TextUtil.isEmpty(start[0]) || TextUtil.isEmpty(start[1])) {

						String key = "schedstartdate";

						containsKey = field.containsKey(key);

						if (!containsKey) {// key 不对可能是其他时间的key
							key = "plandate";
							containsKey = field.containsKey(key);
						}

						if (containsKey) {
							ItemWoListAttribute itemWoListAttribute = field.get(key);
							String data = itemWoListAttribute.getDisValue();
							startTimes = data + " " + startTimes;
							try {
								start = DateUtil.convertyyMMddhhss(startTimes);// 转换为正确的是开始时间
							} catch (Exception e) {
							}

						}
					}
					// 防止没有年月日
					if (end == null || TextUtil.isEmpty(end[0]) || TextUtil.isEmpty(end[1])) {

						String key = "schedstartdate";
						containsKey = field.containsKey(key);

						if (!containsKey) {// key 不对可能是其他时间的key
							key = "plandate";
							containsKey = field.containsKey(key);
						}

						if (containsKey) {
							ItemWoListAttribute itemWoListAttribute = field.get(key);
							String data = itemWoListAttribute.getDisValue();
							endTimes = data + " " + endTimes;

							try {
								end = DateUtil.convertyyMMddhhss(endTimes);
							} catch (Exception e) {
							}

						}
					}

					long upT = getTime(start[0], start[1]);// 获取的是上道的时间的毫秒值
					long downT = getTime(end[0], end[1]);// 获取的是下道的时间的毫秒值

					long absupT = Math.abs(currTime - upT);
					long absdownT = Math.abs(currTime - downT);// 算出当前的时间离上道或者下道谁近

					min = Math.min(absupT, absdownT);// 取出最小的毫秒值，

					if (upT == 0 || downT == 0) {
						min = 0;
					}

					start = null;
					end = null;
				}

				if (work == null) {
					work = wli;
					nearTimeWonum = startTimes;
				}

				if (min < mins && min > 1) {// 如果当前的毫秒值小于他代表他是最小的

					work = wli;
					mins = min;

					nearTimeWonum = startTimes;
				}
			}

			if (work != null) {

				SpUtiles.NearWorkListInfo.setAll(work);

				return nearTimeWonum;
			} else {

				return "";
			}
		}
	}
}
