package com.jtv.base.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jtv.base.ui.KCalendar;
import com.jtv.base.ui.KCalendar.OnCalendarClickListener;
import com.jtv.base.ui.KCalendar.OnCalendarDateChangedListener;
import com.plutus.libraryui.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class BaseCalenderAty extends Activity implements OnClickListener {

	private String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式
	private TextView popupwindow_calendar_month;
	private KCalendar calendar;
	private List<String> list = new ArrayList<String>(); // 设置标记列表
	private TextView tv_back;
	public static final int CALENDER_RESULT = 0X09901;// 响应码

	public static final String CALENDER_DATA = "calender_data";// 响应数据key

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.jtv_base_calendar);
		popupwindow_calendar_month = (TextView) findViewById(R.id.popupwindow_calendar_month);
		calendar = (KCalendar) findViewById(R.id.popupwindow_calendar);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
		popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年" + calendar.getCalendarMonth() + "月");

		// int day = calendar.getDay();
		// System.out.println("天数"+day);

		if (null != date) {

			int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
			int month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
			popupwindow_calendar_month.setText(years + "年" + month + "月");

			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date, R.drawable.calendar_date_focused);
		}
		// 模拟背景
		// list.add("2015-05-01");
		// list.add("2015-05-02");
		// calendar.addMarks(list, 0);

		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer
						.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1, dateFormat.lastIndexOf("-")));

				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();

				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();

				} else {
					// list.add(dateFormat);
					// calendar.addMarks(list, 0);
					// calendar.removeAllBgColor();
					calendar.setCalendarDayBgColor(dateFormat, R.drawable.calendar_date_focused);
					date = dateFormat;// 最后返回给全局 date
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date2 = null;
					try {
						date2 = simpleDateFormat.parse(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Calendar instance = Calendar.getInstance();
					instance.setTime(date2);
					resultCalendar(instance);
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				popupwindow_calendar_month.setText(year + "年" + month + "月");
			}
		});
	}

	public void resultCalendar(Calendar date) {
		Intent resonpse = new Intent();

		resonpse.putExtra(CALENDER_DATA, date);

		setResult(CALENDER_RESULT, resonpse);

		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_back) {
			finish();
		}
	}

}
