package com.evercare.app.util;

/**
 * 作者：xlren on 2016/8/29 13:25
 * 邮箱：renxianliang@126.com
 * 通用常量
 */

public class Constant {
    public static final int ON_REFRESH = 1;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;
    public static final int IMG_ROW = 10;//图片列表的条目数量
    public static final int TEXT_ROW = 20;//无图片列表数量

    public static final String PRIVATE_SEA = "2";//私海
    public static final String PUBLIC_SEA = "3";//公海
    public static final String TEMPORARY = "1";//临时
    public static final String RELATECUSTOMER = "4";//关联客户

    public static final String TIME_ERR = "10005";
    public static final boolean BAFFLE = true;


//    public static final String BASEURL = "http://192.168.1.174:8000";//内部测试机测试
//    public static final String BASEURL_IMG = "http://192.168.1.174:8002";//内部测试机测试
    public static final String BASEURL = "http://192.168.1.202:8000";//内部测试机测试
    public static final String BASEURL_IMG = "http://192.168.1.202:8002";//内部测试机测试
//    public static final String BASEURL = "https://crmtob.evercare.com.cn";//线上地址
//    public static final String BASEURL_IMG = "http://crmsource.evercare.com.cn";//线上地址

    /**
     * CRM接口
     **/
    public static final String INDEX_TEXT = "/index/test";//测试https
    public static final String LOGIN = "/consultant/login";//用户登录
    public static final String CONSULTANT_EDIT_ALIAS_ID = "/Consultant/edit_alias_id";//通知后台设置别名成功
    public static final String CHECK_UPDATE = "/AppVersion/crm_version";//检查版本更新
    public static final String GET_ACTIVITY_LIST = "/activities/list";//获取活动列表
    public static final String GET_CASE_LIST = "/cases/list";//获取案例列表
    public static final String GET_USER_APPOINTMENT = "/my/get_user_appointment";//获取我的预约
    public static final String AGREE_APPOINTMENT = "/my/agree_appointment";//同意预约
    public static final String CANCLE_APPOINTMENT = "/my/cancle_appointment";//取消预约
    public static final String DELETE_APPOINTMENT = "/my/delete_appointment";//删除预约
    public static final String SALESORDER_INDEX = "/SalesOrder/index";//今日已开单人员列表
    //客户模块
    public static final String CUSTOM_INDEX = "/custom/index";//客户首页临时、私有库

    public static final String CUSTOM_RELATION = "/custom/relation";//关联客户

    public static final String CUSTOM_DETAIL = "/custom/detail";//客户详情
    public static final String CUSTOM_RELEASE = "/custom/release";//客户释放
    public static final String CUSTOM_BIRTHDAY = "/custom/birthday";//客户生日
    public static final String CUSTOM_EXPIRING = "/custom/expiring";//即将到公海客户
    public static final String ARRIVETRIAGE_INDEX = "/ArriveTriage/index";//今日可开单
    public static final String INDEX_INDEX = "/index/index";//首页列表数据

    public static final String TASK_INDEX = "/task/index";//今日工作 活动回访 其他回访
    public static final String TASKPLAN_INDEX = "/TaskPlan/index";//计划任务列表
    public static final String TASK_DETAIL = "/task/detail";//今日工作详情页 活动回访详情页 其他回访详情页
    public static final String ARRIVETRIAGEDETAIL = "/ArriveTriage/detail";//开单详情
    public static final String CUSTOM_NEW = "/custom/new";//新分客户

    public static final String CUSTOM_RECORD = "/custom/record";//客户详情 消费/治疗  old
    public static final String CUSTOM_CONSUMPTION = "/custom/record_consumption";//客户详情 消费记录
    public static final String CUSTOM_PROJECT = "/custom/project";//客户详情 订购项目表
    public static final String CUSTOM_CONSULT = "/custom/consult";//客户详情咨询/回访

    //发现模块
    public static final String PROJECTS_LIST = "/priceLevel/list";//价目列表
    public static final String PRODUCT_LIST = "/product/list";//搜索价目列表

    //首页
    public static final String ACHIEVEMENT_INDEX = "/achievement/index";//销售额业绩详情
    public static final String ACHIEVEMENT_TRANSACTION = "/achievement/transaction";//销售额业绩详情
    public static final String CONSULTINGPRODUCT_LIST = "/ConsultingProduct/list";//咨询项目列表接口
    //咨询
    public static final String CHAT_FRIENDS = "/chat/friends";//好友列表

    //任务
    public static final String TASK_CREATE = "/task/create";//创建跟进3
    public static final String APPOINTMENT_CREATE = "/appointment/create";//创建预约
    public static final String CONSULTANT_ADDCONSULT = "/consultant/addconsult";//创建咨询
    public static final String TASK_MODIFY = "/task/modify";//修改跟进任务时间
    public static final String APPOINTMENT_MODIFY = "/appointment/modify";//修改预约时间
    public static final String TASK_CANCEL = "/task/cancel";//任务取消
    public static final String TASK_DELAY = "/task/delay";//跟进任务推迟
    public static final String TASK_START = "/task/start";//任务开始接口
    public static final String APPOINTMENT_DELAY = "/appointment/delay";//预约推迟
    public static final String TASK_FINISH = "/task/finish";//任务完成

    public static final String ARRIVETRIAGE_START = "/ArriveTriage/start";//今日可开单开始接口

    public static final String ARRIVETRIAGE_FINISH = "/ArriveTriage/finish";//今日可开单  保存

    public static final String SALESORDER_DETAIL = "/SalesOrder/detail";//今日已开单客户详细页
    public static final String APPOINTMENT_TASK = "/appointment/task";//约咨询

    public static final String APPOINTMENT_CANCEL = "/appointment/cancel";//预约取消

    public static final String ANSWERS_LIST = "/answers/list";//获取随诊列表
    public static final String ANSWERS_REPLY = "/answers/reply";//回复回话
    public static final String ANSWERS_READ = "/answers/read";//回话已读

    public static final String ARRIVERTRIAGE_CREATE = "/ArriveTriage/create";//开单事件

    public static final String PUSH_MESSAGE = "/Push/crm_message";//获取极光推送消息列表
    public static final String PUSH_READ = "/Push/read";//消息修改已读状态接口
}
