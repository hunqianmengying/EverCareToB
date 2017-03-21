package com.evercare.app.util;

import com.evercare.app.model.Friend;

import java.util.Comparator;


/**
 * 作者：LXQ on 2016-12-8 2016-12-8
 * 邮箱：842202389@qq.com
 * 汉字拼音解析
 */
public class PinyinComparator implements Comparator<Friend> {


	public static PinyinComparator instance = null;

	public static PinyinComparator getInstance() {
		if (instance == null) {
			instance = new PinyinComparator();
		}
		return instance;
	}

	public int compare(Friend lhs, Friend rhs) {
		int lhs_ascii = lhs.getFirstLetter().toUpperCase().charAt(0);
		int rhs_ascii = rhs.getFirstLetter().toUpperCase().charAt(0);
		// 判断若不是字母，则排在字母之后
		if (lhs_ascii < 65 || lhs_ascii > 90)
			return 1;
		else if (rhs_ascii < 65 || rhs_ascii > 90)
			return -1;
		else
			return lhs.getSpelling().compareTo(rhs.getSpelling());
	}

}
