package com.zm.project_template.common.constant;


import lombok.Getter;
import lombok.Setter;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2019/1/17 21:11
 */
public class CommonConstant {

    public static final String TOKEN_KEY = "A";

    public static final String PROD = "prod";

    public static final String INIT_PASSWORD = "123456";

    public static final int STATUS_OK = 1;  // ok

    public static final int STATUS_ERROR = 0;   // error

    public static final int STATUS_VALID = 2;   // 身份待认证

    public static final int MEDIA_IMAGE = 1;    //图片

    public static final int MEDIA_VIDEO = 2;    //视频

    public static final int MEDIA_MUSIC = 3;    //音频

    public static final int MAGIC_ZERO = 0;  // 全局魔法值0

    public static final int MAGIC_ONE = 1;  // 全局魔法值 1

    public static final int MAGIC_TWO = 2;  // 全局魔法值 2

    public static final Integer MAGIC_THREE = 3;  // 全局魔法值 3

    public static final String MAGIC_SPLIT = ",";  // 全局魔法值 ，

    public static final String ROLE_ADMIN = "R_ADMIN";       // 管理员

    public static final String ROLE_USER = "R_USER";       // 普通用户

    public static final String LIMIT_USER = "LIMIT_USER";       // 用户限流

    public static final String LIMIT_ALL = "LIMIT_ALL";       // 所有用户限流 接口每秒请求限制

    public static final String LIMIT_USER_IP = "LIMIT_USER_IP";       // IP限流

    /**
     * 媒体类型
     */
    public enum MEDIA_TYPE {
        MEDIA_IMAGE(1, "图片"),
        MEDIA_VIDEO(2, "视频"),
        MEDIA_MUSIC(3, "音频");

        private Integer code;

        private String value;

        MEDIA_TYPE(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public static String getValue(int code) {
            MEDIA_TYPE[] values = MEDIA_TYPE.values();
            for (MEDIA_TYPE m : values) {
                if (m.code == code) {
                    return m.value;
                }
            }
            return null;
        }
    }

    /**
     * 角色类型
     */
    public enum ROLE {
        ROLE_ADMIN("R_ADMIN", " 管理员"),
        ROLE_USER("R_USER", "普通用户");

        @Setter
        @Getter
        private String code;

        @Setter
        @Getter
        private String name;

        ROLE(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static String getName(String code) {
            ROLE[] values = ROLE.values();
            for (ROLE m : values) {
                if (m.code.equals(code)) {
                    return m.name;
                }
            }
            return null;
        }

        public static String getCode(String name) {
            ROLE[] values = ROLE.values();
            for (ROLE m : values) {
                if (m.code.equals(name)) {
                    return m.name;
                }
            }
            return null;
        }
    }

    /**
     * 月份
     */
    public enum MONTH {
        January("JAN", "01"),
        February("FEB", "02"),
        March("MAR", "03"),
        April("APR", "04"),
        May("MAY", "05"),
        June("JUN", "06"),
        July("JUL", "07"),
        August("AUG", "08"),
        September("SEP", "09"),
        October("OCT", "10"),
        November("NOV", "11"),
        December("DEC", "12");

        @Setter
        String monthEn;

        @Setter
        String monthNum;

        MONTH(String monthEn, String monthNum) {
            this.monthEn = monthEn;
            this.monthNum = monthNum;
        }

        public static String getMonthNum(String monthEn) {
            MONTH[] values = MONTH.values();
            for (MONTH m : values) {
                if (m.monthEn.equalsIgnoreCase(monthEn)) {
                    return m.monthNum;
                }
            }
            return "01";
        }
    }


}
