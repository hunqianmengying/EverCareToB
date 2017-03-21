package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 客户生日Model
 */
public class CustomerBirthdayInfo {

    private List<BirthdayInfo> birthday;

    private List<BirthdayInfo> birthweek;

    public List<BirthdayInfo> getBirthday() {
        return birthday;
    }

    public void setBirthday(List<BirthdayInfo> birthday) {
        this.birthday = birthday;
    }

    public List<BirthdayInfo> getBirthweek() {
        return birthweek;
    }

    public void setBirthweek(List<BirthdayInfo> birthweek) {
        this.birthweek = birthweek;
    }
}
