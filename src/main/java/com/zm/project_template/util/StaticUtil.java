package com.zm.project_template.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2019/1/16 22:04
 */
public class StaticUtil {

    /**
     * 业务线程池
     */
    public static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("business-pool-%d").build();

    public static ExecutorService executorService = new ThreadPoolExecutor(5, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static final List<String> MEDIA_IAMGE = Lists.newArrayList("jpg", "png", "jpeg", "gmp", "gif");

    public static final List<String> MEDIA_VIDEO = Lists.newArrayList("mp4", "avi", "mpeg", "flv", "wmv", "rmvb");

    public static final List<String> MEDIA_MUSIC = Lists.newArrayList("mp3", "aac", "wav", "wma");

    static {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    //常规UUID
    public static String UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //UUID的hashcode +随机数
    public static synchronized String uniqueKey() {
        int abs = Math.abs(Integer.parseInt(String.valueOf(StaticUtil.UUID().hashCode())));
        int random = (int) Math.random() * 1000;
        String temp = String.valueOf(random + abs);
        while (temp.length() < 10) {
            temp += "0";
        }
        if (temp.length() > 10) {
            temp = temp.substring(0, 10);
        }
        return temp;
    }

    // 当前时间的唯一key 同一时间不要使用
    public static synchronized String uniqueKeyByTime(Date date) {
        String dateTime = DateUtil.millisToDateTime(date.getTime(), "yyyyMMddHHmmssSSS");
        // 获取当前进程PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        // 获取当前线程号
        long id = Thread.currentThread().getId();
        return (dateTime + pid + id + StaticUtil.uniqueKey()).substring(0, 30);
    }

    // 当前时间的唯一key 同一时间不要使用
    public static synchronized String uniqueKeyByMillis(Date date, Integer lng) {
        long dateTime = date.getTime();
        // 获取当前进程PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        // 获取当前线程号
        long id = Thread.currentThread().getId();
        if (Objects.nonNull(lng)) {
            return (dateTime + pid + id + StaticUtil.uniqueKey()).substring(0, lng >= 30 ? 29 : lng);
        } else {
            return dateTime + pid + id + StaticUtil.uniqueKey();
        }
    }

    // 同一时间使用 唯一id
    public static String getId(Date date) {
        String dateTime = DateUtil.millisToDateTime(date.getTime(), "yyyyMMddHHmm");
        return dateTime + "0" + String.valueOf(IdWorker.getId());
    }

    public static String getToken() {
        return System.currentTimeMillis() + "" + StaticUtil.uniqueKey();
    }

    public static String convertMailContent(String conent, String km, String orderNo) {
        conent = conent.replace("${km}", " " + km + " ");
        conent = conent.replace("${orderNo}", " " + orderNo + " ");
        return conent;
    }

    /**
     * @param code
     * @return
     */
    public static String md5Hex(String code) {
        return DigestUtils.md5Hex(code);
    }

    public static String md5Hex(String code, String charset) {
        return SignUtil.MD5Encode(code, charset);
    }

    public static String md5Hex(byte[] code) {
        return DigestUtils.md5Hex(code);
    }

    /**
     * base64加密
     *
     * @param code
     * @return
     */
    public static String base64Encode(String code) {
        BaseEncoding baseEncoding = BaseEncoding.base64();
        String encode = baseEncoding.encode(code.getBytes());
        return encode;
    }

    /**
     * base64解码
     *
     * @param code
     * @return
     */
    public static String base64Decode(String code) {
        BaseEncoding baseEncoding = BaseEncoding.base64();
        byte[] decode = baseEncoding.decode(code);
        String s = null;
        try {
            s = new String(decode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * @param userId
     * @return
     */
    public static String getLoginToken(Long userId) {
        String userIdHex = StaticUtil.md5Hex(userId + CommonConstant.TOKEN_KEY);
        return userIdHex;
    }

    public static String sha1512Hex(String code) {
        return DigestUtils.sha512Hex(code);
    }

    /**
     * @param dividend
     * @param divisor
     * @return 相除
     */
    public static Double divide(Double dividend, Double divisor) {
        return dividend == 0.0D && divisor == 0.0D ? 0.0D : divisor == 0.0D ? 1.0D : BigDecimal.valueOf(dividend).divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param multiplicand
     * @param multiplier
     * @return 相乘
     */
    public static Double multiply(Double multiplicand, Double multiplier) {
        return multiplicand == 0.0D && multiplier == 0.0D ? 0.0D : multiplier == 0.0D ? 1.0D : BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(multiplier)).doubleValue();
    }

    public static Long multiplyToLong(Double multiplicand, Double multiplier) {
        return BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(multiplier)).longValue();
    }

    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * @param orderNo
     * @return 数字匹配
     */
    public static boolean checkNum(String orderNo) {
        return orderNo.matches("^[0-9]*$");
    }

    /**
     * @param list
     * @return 加逗号
     */
    public static String joinQuota(List<?> list) {
        if (list.size() == 1) {
            return "'" + list.get(0) + "'";
        }
        if (list.size() == 0) {
            return "";
        }
        final String[] temp = {""};
        list.stream().forEach(s -> temp[0] += "'" + s + "',");
        String result = temp[0].substring(0, temp[0].length() - 1);
        return result;
    }


    /**
     * @param comment
     * @return 根据顺序赋值
     */
    public static <T> T of(String[] comment, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            // 跳过第一个字段
            for (int i = 1; i <= declaredFields.length - 1; i++) {
                Field field = declaredFields[i];
                String name = field.getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                Method method = clazz.getDeclaredMethod("set" + name, String.class);
                try {
                    String value = comment[i - 1];  // 这里-1 因为i从1开始
                    if (StringUtils.isBlank(value)) {
                        method.invoke(obj, "");
                    } else {
                        method.invoke(obj, value);
                    }
                    System.out.println(comment[1]);
                } catch (Exception e) {
                    return obj;
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map readToMap(String src, String field) {
        Map map = new HashMap();
        try {
            map = objectMapper.readValue(src, HashMap.class);
        } catch (IOException e) {
            throw new CommonException(DefinedCode.JSON_ERROR, "字段：" + field + " JSON格式异常！");
        }
        return map;
    }

    public static List readToList(String src, String field) {
        List list = Lists.newArrayList();
        try {
            list = objectMapper.readValue(src, List.class);
        } catch (IOException e) {
            throw new CommonException(DefinedCode.JSON_ERROR, "字段：" + field + " JSON格式异常！");
        }
        return list;
    }

    public static <T> T readToClass(String src, Class<T> tClass) {
        try {
            T t = objectMapper.readValue(src, tClass);
            return t;
        } catch (IOException e) {
            throw new CommonException(DefinedCode.JSON_ERROR, "JavaType转换异常！");
        }
    }

    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    /**
     * AES加密字符串
     *
     * @param content 需要被加密的字符串
     * @param KEY     加密需要的密钥
     * @return 密文
     */
    public static String AesEncode(String content, String KEY) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        String encrypt = AESUtil.encrypt(content, KEY);
        return encrypt;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param code AES加密过过的内容
     * @param KEY  加密时的密钥
     * @return 明文
     */
    public static String decrypt(String code, String KEY) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        String encrypt = AESUtil.decrypt(code, KEY);
        return encrypt;
    }

    /**
     * 生成六位验证码
     * length 长度
     *
     * @return
     */
    public static String genSmsCode(int length) {
        return StaticUtil.uniqueKey().substring(0, length);
    }

    /**
     * 验证字段
     *
     * @param args
     */
    public static void validateField(String... args) {
        List<String> list = Arrays.asList(args);
        list.stream().forEach(o -> {
            if (StringUtils.isBlank(o)) {
                throw new CommonException(DefinedCode.PARAMSERROR, "请填写必填项！");
            }
        });
    }

    /**
     * 验证对象
     *
     * @param args
     */
    public static void validateObject(Object... args) {
        List<Object> list = Arrays.asList(args);
        list.stream().forEach(o -> {
            if (Objects.isNull(o)) {
                throw new CommonException(DefinedCode.PARAMSERROR, "请填写必填项！");
            }
        });
    }

    /**
     * 按顺序设置map
     *
     * @param keys
     * @param values
     * @return
     */
    public static Map<String, Object> multiSetMap(List<String> keys, List<Object> values) {
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

}
