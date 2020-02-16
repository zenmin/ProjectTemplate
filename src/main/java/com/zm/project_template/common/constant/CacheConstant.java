package com.zm.project_template.common.constant;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2019/4/2 17:33
 */
public class CacheConstant {

    public static final String USER_TOKEN_CODE = "USER:TOKEN:";   // 用户登录前缀

    public static final String USER_TOKEN_MAP = "USER:TOKENMAP:";   // 用户id token map

    public static final Long LOCK_SCHEDULING_TIMEOUT = 10L; // 锁过期时间

    public static final String LOCK_SCHEDULING_MONTH = "LOCK:SCHEDULING:MONTH"; //每月定时任务锁

    public static final String LOCK_SCHEDULING_HOURS = "LOCK:SCHEDULING:HOURS"; //每小时定时任务锁

    public static final String LOCK_SCHEDULING_DAY = "LOCK:SCHEDULING:DAY"; //每天定时任务锁

    public static final String LOCK_SCHEDULING_WEEK = "LOCK:SCHEDULING:WEEK";   //每周定时任务锁

    public static final Long EXPIRE_LOGON_TIME = 60 * 60L * 7;           // 登录失效时间一小时

    public static final String ROLE_CACHE = "ROLE";     // 角色信息缓存

    public static final String USER = "USER"; // 用户信息缓存

    public static final String LABEL_HOT_COMMON = "LABEL:HOT:COMMON:"; // 公共热门标签缓存

    public static final String LABEL_HOT_USER = "LABEL:HOT:USER:"; // 用户热门标签缓存

    public static final String RESOURCE_DOWNLOAD_STATUS = "RESOURCE:DOWNLOADSTATUS"; // 允许下载状态

    public static final String LOGIN_PHONE_CODE = "USER:LOGIN:PHONE:";          // 验证码储存

    public static final Long EXPIRE_SMS_CODE = 60L * 5;                      // 验证码有效期 秒

    public static final String USER_VERIFICATION_CODE = USER + ":VFCODE:";                      // 校验码有效期

    public static final Long EXPIRE_VF_CODE = 60L * 5;                      // 校验码有效期

    public static final String USER_NO_INCR_KEY = "USER:NOINCR";                      // 用户编码自增key

    public static final Integer USRE_NO_INCR_NUM = 1;                      // 用户编码初始值

    /**
     * 以下为guavaCache key
     */
    public static final String USER_LIMIT = "USER:LIMIT:";     // 单用户限流

    /**
     * 以下为cacheable key
     */
    public static final String SUBJECT_LIST = "SUBJECT";     // 单用户限流



}
