package com.evercare.app.util;

import com.evercare.app.model.PersonInfo;

import java.util.Comparator;

/**
 * 作者：LXQ on 2016-9-13 11:19
 * 邮箱：842202389@qq.com
 * 字母比较器
 */
public class SpellingComparator implements Comparator<PersonInfo> {
    @Override
    public int compare(PersonInfo lhs, PersonInfo rhs) {
        return sort(lhs, rhs);
    }

    private int sort(PersonInfo lhs, PersonInfo rhs) {
        int lhs_ascii = lhs.getFirstLetter().toUpperCase().charAt(0);
        int rhs_ascii = rhs.getFirstLetter().toUpperCase().charAt(0);
        // 判断若不是字母，则排在字母之后
        if (lhs_ascii < 65 || lhs_ascii > 90)
            return 1;
        else if (rhs_ascii < 65 || rhs_ascii > 90)
            return -1;
        else
            return lhs.getFirstLetter().compareTo( rhs.getFirstLetter());// 正确的方式
    }
}
