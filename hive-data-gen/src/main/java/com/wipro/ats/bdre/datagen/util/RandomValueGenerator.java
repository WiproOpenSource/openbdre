/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen.util;

import com.wipro.ats.bdre.datagen.xeger.Xeger;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 */
public class RandomValueGenerator {
	

	public static Random random = new Random();
	private static final Logger LOGGER=Logger.getLogger(RandomValueGenerator.class);

	
	/**
	 * 
	 * @return a random number
	 */
	public static String randomNumber(String arg){
		arg=arg.replaceAll("\'","\"");
		Random r = new Random();
		long low = Long.parseLong(arg.split(",")[0]);
		long high = Long.parseLong(arg.split(",")[1]);
		long offset=low+(long)(r.nextDouble()*(high-low));
		String str = offset +"";
		return str;
	}

	/**
	 * 
	 * @return a random decimal
	 */
	public static String randomDecimal(String arg){
		   StringBuilder randomStringBuilder = new StringBuilder();
		   //Minimum 1 and Maximum 100
		   float decimalvalue=random.nextFloat()*100 + 1;
		   //Precise the float value with a precision of 2
		   randomStringBuilder.append(Math.round(decimalvalue*100.0)/100.0);
		   return randomStringBuilder.toString();
	}
	public static String randomRegexPattern(String pattern) {
		Xeger generator = new Xeger(pattern);
		String result = generator.generate();
		return result;
	}
	public static String randomDate(String arg){
		arg=arg.replaceAll("\'","\"");
		long low = Long.parseLong(arg.split(",")[0]);
		long high = Long.parseLong(arg.split(",")[1]);
		String format = arg.split(",")[2];
		Random r = new Random();
		long offset=low+(long)(r.nextDouble()*(high-low));
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
		Date date=new Date(offset);
		return simpleDateFormat.format(date);

	}
}
