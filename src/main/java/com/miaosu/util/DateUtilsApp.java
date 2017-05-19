package com.miaosu.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtilsApp {
	/**
	 * 字符串转换为日期（包含小时分）
	 * @param str 日期字符串
	 * @param pattern 时间模式 为null时默认为“yyy-MM-dd HH:mm:ss”       
	 * @return Date
	 */
	public final static Date StrToDateTime(String str, String pattern) {
		Date returnDate = null;
		if (pattern == null) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			returnDate = sdf.parse(str);
		} catch (Exception e) {
			return returnDate;
		}
		return returnDate;
	}
	/**
	 * 字符串转换为日期（包含小时分）yymmddhhmm
	 * @param str
	 * @return Date
	 */
	public final static Date StringToDateTime(String str) {
		StringBuffer tmpStr = new StringBuffer();
		tmpStr.append(getThisYear().substring(0, 2)).append(str.substring(0, 2)).append("-").append(
				str.substring(2, 4)).append("-").append(str.substring(4, 6)).append(" ").append(
				str.substring(6, 8)).append(":").append(str.substring(8, 10));
		return StrToDateTime(tmpStr.toString());
	}
	public final static String getThisYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(new Date());
	}
	/**
	 * 字符串转换为日期（包含小时分）,格式:yyyy-mm
	 * @param str
	 * @return
	 */
	public final static Date StrToDateTime(String str) {
		Date returnDate = null;
		if (str != null && str.trim().length() > 0) {
			DateFormat df = DateFormat.getDateTimeInstance();

			try {
				int strLength = str.length();
				if (strLength < 11) {
					str += " 00:00:00";
				} else if (strLength > 11 && strLength < 14) {
					str += ":00:00";
				} else if (strLength > 14 && strLength < 17) {
					str += ":00";
				}

				returnDate = df.parse(str);
			} catch (Exception e) {
				return returnDate;
			}
		}
		return returnDate;
	}
	/**
	 * 字符串转为日期
	 * @param str
	 * @return
	 * @throws AppException 
	 */
	public final static Date StrToDate(String str){
		Date returnDate = null;
		if (str != null) {
			DateFormat df = DateFormat.getDateInstance();
			try {
				returnDate = df.parse(str);
			} catch (Exception e) {
			}
		}
		return returnDate;
	}
	
	/**
	 * 将日期时间转换成 pattern 格式的时间
	 * @param date 要转换的日期时间
	 * @param pattern 时间模式 为null时默认为“yyyyMMdd”
	 * @return String 转换后的日期时间
	 */
	public final static String getDateTimeStr(Date date, String pattern) {
		if (pattern == null)
			pattern = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	/**
	 * 将日期时间转换成 pattern 格式的时间
	 * @param date 要转换的日期时间
	 * @param pattern 时间模式 为null时默认为“yyy-MM-dd HH:mm:ss”
	 * @return String 转换后的日期时间
	 */
	public final static String getDateTime(Date date, String pattern) {
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	/**
	 * 将日期转换成yyy-MM-dd的格式
	 * @param date 要转换的日期
	 * @return String 转换后的日期
	 */
	public final static String getStrDate(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	/**
	 * 将日期转换成yyyyMM格式
	 * @param date 要转换的日期
	 * @return  转换后的月份
	 */
	public final static String getStrYearMonth(Date date){
		if(date == null){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return sdf.format(date);
	}
	/**
	 * 将日期转换成yyyyMM格式月份
	 * @param date
	 * @return
	 */
	public final static int getNumYearMonth(Date date){
		String yearMonth = getStrYearMonth(date);
		if(yearMonth == ""){
			return 0;
		}else{
			return Integer.parseInt(yearMonth);
		}
	}
	/**
	 * 将日期时间转换成yyy-MM-dd HH:mm:ss的格式
	 * @param date 要转换的日期时间
	 * @return 转换后的日期时间
	 */
	public final static String getStrDateTime(Date date) {
		SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return nowDate.format(date);
	}
	/**
	 * 获得当前日期
	 * @return 当前日期，格式：yyyy-MM-dd
	 */
	public final static String getCurrStrDate() {
		SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd");
		return nowDate.format(new Date());
	}
	/**
	 * 获得当前日期和时间
	 * @return 当前日期和时间，格式：yyyy-MM-dd HH:mm:ss
	 */
	public final static String getCurrStrDateTime() {
		/*SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return nowDate.format(new Date());*/
		return getCurrDateTime("yyyy-MM-dd HH:mm:ss");
	}
	public final static String getCurrDateTime(String pattern){
		try
		{
			SimpleDateFormat nowDate = new SimpleDateFormat(pattern);
			return nowDate.format(new Date());
		}
		catch (Exception e)
		{
		}
		return null;
	}
	public final static Date getCurrDate(){
		return StrToDateTime(getCurrStrDate(),"yyyy-MM-dd");
	}
	public final static Date getCurrDatetime(){
		return StrToDateTime(getCurrStrDateTime(),"yyyy-MM-dd HH:mm:ss");
	}
	public static String conver2Date(String str)
	{
		String date = null;
		if(str == null || str.trim().length() == 0)
		{
			return date;
		}
		String pattern = "yyyy-MM-dd HH:mm:ss";
		date = convertDateStrToStr(str, "yyyy-MM-dd HH:mm:ss", pattern);
		if(date != null)
		{
			return date;
		}
		date = convertDateStrToStr(str, "yyyy-MM-dd HH:mm", pattern);
		if(date != null)
		{
			return date;
		}
		date = convertDateStrToStr(str, "yyyy-MM-dd", pattern);
		if(date != null)
		{
			return date;
		}
		date = convertDateStrToStr(str, "yyyyMMdd", pattern);
		if(date != null)
		{
			return date;
		}
		date = convertDateStrToStr(str, "yyyy-M-d", pattern);
		if(date != null)
		{
			return date;
		}
		date = convertDateStrToStr(str, "yyyyMd", pattern);
		if(date != null)
		{
			return date;
		}
		return date;
	}
	public static String convertDateStrToStr(String dateStr, String oldPattern, String newPattern)
	{
		try
		{
			SimpleDateFormat nowDate = new SimpleDateFormat(oldPattern);
			return getDateTime(nowDate.parse(dateStr), newPattern);
		}
		catch (Exception e)
		{
		}
		return null;
	}
	/**
	 * 当前日期加减amount天，然后按pattern格式返回
	 * @param pattern
	 * @param amount
	 * @return
	 */
	public static String addCurrDateToStr(String pattern,int amount){
		Calendar currCal = Calendar.getInstance();
		currCal.add(Calendar.DAY_OF_MONTH, amount);
		SimpleDateFormat nowDate = new SimpleDateFormat(pattern);
		return nowDate.format(currCal.getTime());
	}
	/**
	 * 得到下半月开始日期
	 * @param date
	 * @return
	 */
	public static Date getNextHalfMonthDate(Date date,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if(day < 16){
			cal.set(Calendar.DAY_OF_MONTH, 16);
		}else{
			cal.set(Calendar.DAY_OF_MONTH,1);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		}
		
		return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
	}
	/**
	 * 获取下月第一天
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date getNextMonthFirstDate(Date date,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		cal.set(Calendar.DAY_OF_MONTH,1);
		return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
	}
	
	/**
	 * 获取本月最后一天
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date getMonthLastDate(Date date,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getNextMonthFirstDate(date,pattern));
		cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH) -1);
		return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
	}
	
	/**
	 * 获取本月第一天
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date getMonthFirstDate(Date date,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH,1);
		return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
	}
	
	/**
	 * 获取上月第一天
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date getLastMonthFirstDate(Date date,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DAY_OF_MONTH,1);
		return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
	}
	
	/**
	 * 获取上月最后一天
	 * @param date
	 * @param pattern
	 * @return
	 */
public static Date getLastMonthLastDate(Date date,String pattern){
	Calendar cal = Calendar.getInstance();
	cal.setTime(getMonthFirstDate(date,pattern));
	cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH) -1);
	return StrToDateTime(getDateTime(cal.getTime(), pattern),pattern);
}

/**
 * 比较两个日期是否是同一天；
 * @param day1
 * @param day2
 * @return
 */
public  static boolean isSameDay(Date day1, Date day2) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String ds1 = sdf.format(day1);
    String ds2 = sdf.format(day2);
    if (ds1.equals(ds2)) {
        return true;
    } else {
        return false;
    }
}
/**
 * 当前日期加减minute分钟
 * @param pattern
 * @param amount
 * @return
 */
public static Date addCurrDateTime(int minute){
	Calendar currCal = Calendar.getInstance();
	currCal.add(Calendar.MINUTE, minute);
	return currCal.getTime();
}


	/**
	 * 当前日期加减amount天，然后按pattern格式返回
	 * @param pattern
	 * @param amount
	 * @return
	 */
	public static Date addCurrDate(int amount){
		Calendar currCal = Calendar.getInstance();
		currCal.add(Calendar.DAY_OF_MONTH, amount);
		return currCal.getTime();
	}
	/**
	 * 日期加减amount天
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addDate(Date date,int amount){
		Calendar currCal = Calendar.getInstance();
		currCal.setTime(date);
		currCal.add(Calendar.DAY_OF_MONTH, amount);
		return currCal.getTime();
	}
	/**
	 * 获取(stopDate-startDate)的间隔天数
	 * @param startDateStr yyyy-MM-dd
	 * @param stopDateStr yyyy-MM-dd
	 * @return
	 * @throws AppException 
	 */
	public static long getIdleDay(String startDateStr, String stopDateStr){
		Date sDate = StrToDate(startDateStr);
		Date eDate = StrToDate(stopDateStr);
		return getIdleDay(sDate,eDate);
	}
	/**
	 * 获取(stopDate-startDate)的间隔天数
	 * @param startDateStr yyyy-MM-dd
	 * @param stopDateStr yyyy-MM-dd
	 * @return
	 */
	public static long getIdleDay(Date sDate,Date eDate){
		if(sDate == null || eDate == null){
			return -1;
		}
		long idleDay = (eDate.getTime() - sDate.getTime())/1000/3600/24;
		
		
		return idleDay;
	}
	/**
	 * 判断HH:MM 时间合法性
	 * 
	 * @param hhss
	 * @return
	 */
	public static boolean isLegalHHMM(String hhss) {
		String reg = "(^([0-1][0-9])|2[0-3]):[0-5][0-9]$";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(hhss);
		return matcher.matches();
	}
	/**
	 * 获取当前系统上一月字符串：格式：yyyy-mm
	 * @return
	 */
	public static String getPreviousMonth(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return getDateTime(cal.getTime(),"yyyy-MM");
	}
	public static String getCurrMonth(){
		return getCurrDateTime("yyyy-MM");
	}
	public static String getPreviousMonth(String month){
		return getPreviousMonth(StrToDateTime(month, "yyyy-MM"));
	}
	/**
	 * 数字月份加减月数
	 * @param month
	 * @param num
	 * @return
	 */
	public static int addMonth(int month,int num){
		return Integer.parseInt(addMonth(Integer.toString(month),num,"yyyyMM"));
	}
	/**
	 * 月数加月数，返回月份
	 * @param month
	 * @param num
	 * @return 月份(yyyy-MM)
	 */
	public static String addMonth(String month,int num){
		return addMonth(StrToDateTime(month, "yyyy-MM"),num);
	}
	/**
	 * 月份加月数，返回月份
	 * @param month  格式由pattern指定
	 * @param num
	 * @param pattern
	 * @return 返回格式由pattern格式指定
	 */
	public static String addMonth(String month,int num,String pattern){
		return addMonth(StrToDateTime(month, pattern),num,pattern);
	}
	/**
	 * 日期加月份
	 * @param date
	 * @param num
	 * @return  yyyy-MM格式
	 */
	public static String addMonth(Date date,int num){
		return addMonth(date,num,"yyyy-MM");
	}
	/**
	 * 日期加月数，返回月份
	 * @param date
	 * @param num
	 * @param pattern
	 * @return
	 */
	public static String addMonth(Date date,int num,String pattern){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, num);
		return getDateTime(cal.getTime(),pattern);
	}
	public static String getPreviousMonth(Date date){
		return addMonth(date,-1);
	} 
	/**
	 * 获取月底字符串
	 * @param month 月，格式：yyyy-MM
	 * @return 月底字符串，格式：yyyy-MM-dd
	 */
	public static String getEndMonth(String month){
		Date date = StrToDateTime(month + "-01","yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return getDateTime(cal.getTime(),"yyyy-MM-dd");
	}
	
		
	public static void main(String[] args){
//		System.out.println(getDateTime(new Date(),"yyyyMMdd"));
//		Date date = StrToDateTime("2013-11-11", "yyyy-MM-dd");
//		System.out.println(getCurrDateTime("yyyy-MM-dd"));
		/*System.out.println(getIdleDay("2013-11-11", "2013-11-11"));
		System.out.println(getIdleDay("2013-11-11", "2013-11-18"));
		System.out.println(getIdleDay("2013-11-19", "2013-11-17"));*/
		//Date date = StrToDateTime("2014-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		//System.out.println(getEndMonth("2014-06"));
		//String previousMonth = DateUtils.getPreviousMonth();
		//String startDate = previousMonth + "-01";
		//String endDate = DateUtils.getEndMonth(previousMonth);
		//System.out.println(startDate);
		//System.out.println(endDate);
		String pattern = "yyyy-MM-dd";
		Date currDate = StrToDateTime("2014-12-16", pattern);
		System.out.println(getDateTime(currDate, pattern));
		Date endDate = getNextHalfMonthDate(currDate,pattern);
		System.out.println(getIdleDay(currDate,endDate));
	}
}
