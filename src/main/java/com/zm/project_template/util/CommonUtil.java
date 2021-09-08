package com.zm.project_template.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.common.constant.RequestConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2019/1/16 22:04
 */
@Slf4j
public class CommonUtil {

    /**
     * 业务线程池
     */
    public static ThreadFactory businessThreadFactory = new ThreadFactoryBuilder().setNameFormat("business-pool-%d").build();

    public static ExecutorService executorService = new ThreadPoolExecutor(5, 100, 10, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), businessThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 步长为20 即20个数据开一个线程
     */
    public static final Integer THREAD_LENGTH = 20;

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 校验英文
     */
    public static final Pattern COMPILE_ENGLISH = Pattern.compile("[A-Za-z]");

    /**
     * 校验中文
     */
    public static final Pattern COMPILE_CHINESE = Pattern.compile("[\u4e00-\u9fa5]");

    /**
     * 校验手机号
     */
    public static final String COMPILE_PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|14[0|1|2|3|4|5|6|7|8|9]|16[0|1|2|3|4|5|6|7|8|9]|17[0|1|2|3|4|5|6|7|8|9]|19[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    /**
     * 校验用户名
     */
    public static final String COMPILE_USERNAME = "^[a-zA-Z][a-zA-Z0-9_]{1,15}$";

    /**
     * SpringSecurity默认密码编码器
     */
    public static final PasswordEncoder BCRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * AES对称加密 默认密钥SYSTEM_KEY
     */
    private static final AES AES = SecureUtil.aes(CommonConstant.SYSTEM_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 请求加密的aes
     */
    private static final AES REQUESTAES = SecureUtil.aes(CommonConstant.SYSTEM_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 常规UUID
     *
     * @return
     */
    public static synchronized String UUID() {
        return IdWorker.get32UUID();
    }

    /**
     * UUID的hashcode +随机数 有几率重复
     *
     * @return
     */
    public static synchronized String uniqueKey() {
        int abs = Math.abs(Integer.parseInt(String.valueOf(CommonUtil.UUID().hashCode())));
        int random = (int) (Math.random() * 1000);
        String temp = String.valueOf(random + abs);
        while (temp.length() < 10) {
            temp += "0";
        }
        if (temp.length() > 10) {
            temp = temp.substring(0, 10);
        }
        return temp;
    }

    /**
     * 当前时间的唯一key 同一时间不要使用
     *
     * @param date
     * @return
     */
    public static synchronized String uniqueKeyByTime(Date date) {
        String dateTime = DateUtil.millisToDateTime(date.getTime(), "yyyyMMddHHmmssSSS");
        // 获取当前进程PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        // 获取当前线程id
        long id = Thread.currentThread().getId();
        return (dateTime + pid + id + CommonUtil.uniqueKey()).substring(0, 30);
    }

    /**
     * 当前时间的唯一key 同一时间不要使用
     *
     * @param date
     * @param lng
     * @return
     */
    public static synchronized String uniqueKeyByMillis(Date date, Integer lng) {
        long dateTime = date.getTime();
        // 获取当前进程PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        // 获取当前线程id
        long id = Thread.currentThread().getId();
        if (Objects.nonNull(lng)) {
            return (dateTime + pid + id + CommonUtil.uniqueKey()).substring(0, lng >= 30 ? 29 : lng);
        } else {
            return dateTime + pid + id + CommonUtil.uniqueKey();
        }
    }

    /**
     * 同一时间使用 唯一id 生成订单号
     *
     * @return
     */
    public static String getTimeId() {
        return IdWorker.getTimeId();
    }

    /**
     * 唯一id
     *
     * @return
     */
    public static String getId() {
        return IdWorker.getIdStr();
    }

    /**
     * 加上咱们salt的md5 用于生成密码之类
     *
     * @param code
     * @return
     */
    public static String md5Hex(String code) {
        try {
            return DigestUtils.md5Hex(code.getBytes(Charsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取SpringSecurity密码
     *
     * @param password
     * @return
     */
    public static String getBCryptPassword(String password) {
        return BCRYPT_PASSWORD_ENCODER.encode(password);
    }

    /**
     * 比对SpringSecurity密码
     *
     * @param rawPassword    未加密的密码
     * @param encodePassword 已加密的密码
     * @return
     */
    public static boolean matchBCryptPassword(String rawPassword, String encodePassword) {
        return BCRYPT_PASSWORD_ENCODER.matches(rawPassword, encodePassword);
    }

    /**
     * 原生md5
     *
     * @param code
     * @return
     */
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
     * base64加密
     *
     * @param code
     * @return
     */
    public static String base64Encode(byte[] code) {
        BaseEncoding baseEncoding = BaseEncoding.base64();
        String encode = baseEncoding.encode(code);
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
            s = new String(decode, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String sha1Hex(String code) {
        return DigestUtils.sha1Hex(code);
    }

    public static String sha256Hex(String code) {
        return DigestUtils.sha256Hex(code);
    }

    public static String sha1512Hex(String code) {
        return DigestUtils.sha512Hex(code);
    }

    /**
     * 相加
     *
     * @param pre
     * @param suff
     * @return
     */
    public static Double add(double pre, double suff) {
        return BigDecimal.valueOf(pre).add(BigDecimal.valueOf(suff)).doubleValue();
    }

    /**
     * 相除 四舍五入 保留两位小数
     *
     * @param dividend
     * @param divisor
     * @return
     */
    public static Double divide(double dividend, double divisor) {
        if (dividend == 0d || divisor == 0d) {
            return 0d;
        }
        return BigDecimal.valueOf(dividend).divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算增长率
     *
     * @param nowTime  本月 或 本年
     * @param lastTime 上月 或 上年
     * @return
     */
    public static Double divideAddRate(Double nowTime, Double lastTime) {
        if (Objects.isNull(nowTime)) {
            nowTime = 0d;
        }
        if (Objects.isNull(lastTime)) {
            lastTime = 0d;
        }
        if (nowTime > 0 && lastTime == 0) {
            return 1d;
        }
        if (nowTime == 0 && lastTime == 0) {
            return 0d;
        }
        double v = BigDecimal.valueOf(subtract(nowTime, lastTime)).divide(BigDecimal.valueOf(lastTime), 4, RoundingMode.HALF_UP).doubleValue();
        if (v < -1) {
            v = -1;
        }
        return v;
    }

    /**
     * 相乘 四舍五入 保留两位小数
     *
     * @param multiplicand
     * @param multiplier
     * @return
     */
    public static Double multiply(double multiplicand, double multiplier) {
        if (multiplicand == 0d || multiplier == 0d) {
            return 0d;
        }
        return BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(multiplier)).doubleValue();
    }

    public static Long multiplyToLong(double multiplicand, double multiplier) {
        if (multiplicand == 0d || multiplier == 0d) {
            return 0L;
        }
        return BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(multiplier)).longValue();
    }

    /**
     * 相减
     *
     * @param multiplicand
     * @param multiplier
     * @return
     */
    public static Double subtract(double multiplicand, double multiplier) {
        return BigDecimal.valueOf(multiplicand).subtract(BigDecimal.valueOf(multiplier)).doubleValue();
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
            return "'" + list.get(0).toString() + "'";
        }
        if (list.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object s : list) {
            stringBuilder.append("'").append(s).append("'").append(",");
        }
        String result = stringBuilder.substring(0, stringBuilder.length() - 1);
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

    /**
     * @param comment
     * @return 根据顺序赋值
     */
    public static <T> T valueOfToClass(List<String> comment, Class<T> clazz) {
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
                    String value = comment.get(i - 1);  // 这里-1 因为i从1开始
                    if (StringUtils.isBlank(value)) {
                        method.invoke(obj, "");
                    } else {
                        method.invoke(obj, value);
                    }
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
            if (StringUtils.isNotBlank(src)) {
                map = objectMapper.readValue(src, HashMap.class);
            }
        } catch (IOException e) {
            throw new CommonException(DefinedCode.JSON_ERROR, "字段：" + field + " JSON格式异常！");
        }
        return map;
    }

    public static List readToList(String src, String field) {
        List list = Lists.newArrayList();
        if (StringUtils.isNotBlank(src)) {
            try {
                list = objectMapper.readValue(src, List.class);
            } catch (IOException e) {
                log.error("字段{}JSON格式异常！", field);
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 耗时500+ms  尽量用JSONObject.parse
     *
     * @param src
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T readToClass(String src, Class<T> tClass) {
        try {
            if (StringUtils.isNotBlank(src)) {
                T t = objectMapper.readValue(src, tClass);
                return t;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException(DefinedCode.JSON_ERROR, "JavaType转换异常！");
        }
        return null;
    }

    public static String unicodeToCn(String unicode) {
        // 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格
        String[] strs = unicode.split("\\\\u");
        StringBuilder returnStr = new StringBuilder();
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr.append((char) Integer.valueOf(strs[i], 16).intValue());
        }
        return returnStr.toString();
    }

    /**
     * AES加密字符串
     *
     * @param content 需要被加密的字符串
     * @param key     加密需要的密钥
     * @return 密文
     */
    public static String aesEncode(String content, String key) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        try {
            if (StringUtils.isNotBlank(key)) {
                AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
                String encrypt = aes.encryptHex(content);
                return encrypt;
            } else {
                String encrypt = AES.encryptHex(content);
                return encrypt;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param code AES加密过过的内容
     * @param key  加密时的密钥
     * @return 明文
     */

    public static String aesDecode(String code, String key) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            if (StringUtils.isNotBlank(key)) {
                AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
                String encrypt = aes.decryptStr(code);
                return encrypt;
            } else {
                String encrypt = AES.decryptStr(code);
                return encrypt;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param code AES加密过过的内容
     * @return 明文
     */

    public static String aesDecodeRequest(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            return REQUESTAES.decryptStr(code);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成六位验证码
     *
     * @param length 长度
     * @return
     */
    public static String genSmsCode(int length) {
        return CommonUtil.uniqueKey().substring(0, length);
    }

    /**
     * 验证字段
     *
     * @param args
     */
    public static void validateField(String... args) {
        List<String> list = Arrays.asList(args);
        list.forEach(o -> {
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
        list.forEach(o -> {
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

    /**
     * SpringMvc下获取当前request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * SpringMvc下获取当前session
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;

    }

    public static String readToString(Map<String, String[]> parameterMap) {
        try {
            return objectMapper.writeValueAsString(parameterMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * @return
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 验证身份证号码
     *
     * @return
     */
    public static void validIdCard(String idCard) {
        if (StringUtils.isNotBlank(idCard)) {
            String reg = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
            if (!idCard.matches(reg)) {
                throw new CommonException(DefinedCode.PARAMSERROR, "身份证号码格式错误！");
            }
        }
    }


    public static void validPassword(String password, String passwordTwo) {
        if (StringUtils.isNotBlank(passwordTwo)) {
            if (!StringUtils.equals(password, passwordTwo)) {
                throw new CommonException(DefinedCode.PARAMSERROR, "两次密码输入不一致！");
            }
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new CommonException(DefinedCode.PARAMSERROR, "密码长度在6~20位之间！");
        }
        if (COMPILE_CHINESE.matcher(password).find()) {
            throw new CommonException(DefinedCode.PARAMSERROR, "密码格式错误，不能包含中文！");
        }
    }

    public static void validPhone(String phone) {
        if (!phone.matches(COMPILE_PHONE)) {
            throw new CommonException(DefinedCode.PARAMSERROR, "手机号有误！");
        }
    }

    public static void validUserName(String username) {
        if (!username.matches(COMPILE_USERNAME)) {
            throw new CommonException(DefinedCode.PARAMSERROR, "格式错误，用户名由2~15位字母/数字/下划线组成！");
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    /**
     * 根据长度计算开多少个线程
     *
     * @param size
     * @return List<Integer> List长度表示多少个线程 Integer表示线程节点的终点
     */
    public static List<Integer> getThreadCount(int size) {
        List<Integer> segment = Lists.newArrayList();
        if (size <= 0) {
            return segment;
        }
        int count = 1;
        if (size > THREAD_LENGTH) {
            count = size / THREAD_LENGTH;
            // 只允许开十个线程  如果大于十个 则最后一个线程处理剩下的所有任务
            if (count < 10) {
                for (int i = 0; i <= count; i++) {
                    segment.add(i * THREAD_LENGTH);
                }
                if (size % THREAD_LENGTH > 0) {
                    segment.add(THREAD_LENGTH * count + size % THREAD_LENGTH);
                }
            } else {
                for (int i = 0; i <= count; i++) {
                    if (i < 10) {
                        segment.add(i * THREAD_LENGTH);
                    } else {
                        int step = i * THREAD_LENGTH;
                        segment.add(step + (size - step));
                        break;
                    }
                }
            }
        } else {
            segment.add(0);
            segment.add(size);
        }
        return segment;
    }

}
