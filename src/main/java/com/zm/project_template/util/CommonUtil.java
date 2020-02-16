package com.zm.project_template.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
    public static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("business-pool-%d").build();

    /**
     * int corePoolSize:线程池维护线程的最小数量.
     * int maximumPoolSize:线程池维护线程的最大数量.
     * long keepAliveTime:空闲线程的存活时间.
     * TimeUnit unit: 时间单位,现有纳秒,微秒,毫秒,秒枚举值.
     * BlockingQueue<Runnable> workQueue:持有等待执行的任务队列.
     * RejectedExecutionHandler handler: 用来拒绝一个任务的执行，有两种情况会发生这种情况。
     * 一是在execute方法中若addIfUnderMaximumPoolSize(command)为false，即线程池已经饱和；
     * 二是在execute方法中, 发现runState!=RUNNING || poolSize == 0,即已经shutdown,就调用ensureQueuedTaskHandled(Runnable command)，在该方法中有可能调用reject。
     */
    public static ExecutorService executorService = new ThreadPoolExecutor(5, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

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
    public static final String COMPILE_PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";


    /**
     * 校验用户名
     */
    public static final String COMPILE_USERNAME = "^[a-zA-Z][a-zA-Z0-9_]{1,15}$";

    /**
     * 常规UUID
     *
     * @return
     */
    public static synchronized String UUID() {
        return IdWorker.get32UUID();
    }

    /**
     * UUID的hashcode +随机数 重复几率:0.000269
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
     * @param date
     * @return
     */
    public static String getId(Date date) {
        String dateTime = DateUtil.millisToDateTime(date.getTime(), "yyyyMMddHHmm");
        return dateTime + "0" + String.valueOf(IdWorker.getId());
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

    public static String md5HexUtf8(String code) {
        try {
            return DigestUtils.md5Hex(code.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return code;
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
    public static String getLoginToken(String userId) {
        String userIdHex = CommonUtil.md5Hex(userId + CommonConstant.TOKEN_KEY);
        return userIdHex;
    }

    public static String sha1Hex(String code) {
        return DigestUtils.sha1Hex(code);
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
     * @param multiplicand
     * @param multiplier
     * @return 相减
     */
    public static Double subtract(Double multiplicand, Double multiplier) {
        Long big = multiplyToLong(multiplicand, 100d);
        Long small = multiplyToLong(multiplier, 100d);
        BigDecimal subtract = BigDecimal.valueOf(big).subtract(BigDecimal.valueOf(small));
        return divide(subtract.doubleValue(), 100d);
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
    public static <T> T of(List<String> comment, Class<T> clazz) {
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
                throw new CommonException(DefinedCode.PARAMS_ERROR, "请填写必填项！");
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
                throw new CommonException(DefinedCode.PARAMS_ERROR, "请填写必填项！");
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
            return URLEncoder.encode(str, "UTF-8");
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
            return URLDecoder.decode(str, "UTF-8");
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
                throw new CommonException(DefinedCode.PARAMS_ERROR, "身份证号码格式错误！");
            }
        }
    }


    public static void validPassword(String password) {
        if (password.length() < 6 || password.length() > 18) {
            throw new CommonException(DefinedCode.PARAMS_ERROR, "密码长度在6~18位之间！");
        }

        if (COMPILE_CHINESE.matcher(password).find()) {
            throw new CommonException(DefinedCode.PARAMS_ERROR, "密码格式错误，不能包含中文！");
        }

    }

    public static void validPhone(String phone) {
        if (!phone.matches(COMPILE_PHONE)) {
            throw new CommonException(DefinedCode.PARAMS_ERROR, "手机号有误！");
        }
    }

    public static void validUserName(String username) {
        if (!username.matches(COMPILE_USERNAME)) {
            throw new CommonException(DefinedCode.PARAMS_ERROR, "格式错误，用户名由2~15位字母/数字/下划线组成！");
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Es 判断是否有英文  有英文前后加上*
     *
     * @param name
     * @return
     */
    public static String fixEsQueryString(String name) {
        name = name.replace(" ", "");
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (CommonUtil.COMPILE_ENGLISH.matcher(String.valueOf(c)).find()) {
                str.append("*" + c + "*");
            } else {
                str.append(c);
            }
        }
        String replace = str.toString().replace("**", "*");
        return replace.toLowerCase();
    }
}
