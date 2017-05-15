package com.miaosu.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

public class DefaultUtil {

	private static final List<String> mobiles = Arrays.asList(new String[] { "134", "135", "136", "137", "138", "139",
			"147", "150", "151", "152", "157", "158", "159", "178", "182", "183", "184", "187", "188" });
	
	private static final List<String> unicoms = Arrays.asList(new String[] { "130", "131", "132", "145", "155", "156",
			"176", "185", "186" });
	
	private static final List<String> telecoms = Arrays
			.asList(new String[] { "133", "153", "177", "180", "181", "189" });
	
	/**
	 * 根据手机号码判断运营商
	 * @param phone
	 * @return
	 */
	public static String getOperator(String phone)
	{
		if(NumberUtils.isDigits(phone) && phone.length() == 11)
		{
			String prefix = phone.substring(0, 3);
			
			if(mobiles.contains(prefix))
			{
				return "移动";
			}
			else if (unicoms.contains(prefix))
			{
				return "联通";
			}
			else if (telecoms.contains(prefix))
			{
				return "电信";
			}
			else 
			{
				return "未知";
			}
		
		}
		return "";
	}
}
