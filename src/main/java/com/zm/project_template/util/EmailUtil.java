package com.zm.project_template.util;

import com.zm.project_template.common.constant.RequestConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Describle This Class Is 邮件发送工具类
 * @Author ZengMin
 * @Date 2019/2/17 13:47
 */
@Component
@Slf4j
public class EmailUtil {

    @Value("${email.sendUsername}")
    String username;

    @Value("${email.sendPassword}")
    String password;

    @Value("${email.sendHost}")
    String host;

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${email.receive}")
    String receive;

    /**
     * 发送邮件主方法 不要直接调
     *
     * @param userTitle
     * @param receiveUser
     * @param content
     */
    @Async
    public void sendMail(String userTitle, String receiveUser, String content) {
        try {
            if (StringUtils.isBlank(userTitle)) {
                userTitle = "艾度评价系统邮件:" + env + "环境";
            }
            if (StringUtils.isBlank(receiveUser)) {
                receiveUser = receive;
            }
            //设置发件属性
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setUsername(username);
            javaMailSender.setPassword(password);
            javaMailSender.setHost(host);

            Properties properties = new Properties();
            properties.setProperty("spring.mail.properties.mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.port", "465");
            properties.setProperty("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            javaMailSender.setDefaultEncoding("UTF-8");
            javaMailSender.setJavaMailProperties(properties);

            //邮件内容
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            //multipart=true 表示这是一个可以上传附件的消息
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, false);
            //收件人地址不对  会抛出550 Invalid Addresses
            mimeMessageHelper.setTo(receiveUser);
            //表明这是一个html片段
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setSubject(userTitle);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setValidateAddresses(false);
            mimeMessageHelper.setSentDate(new Date());
            // 添加邮件附件
            javaMailSender.send(mimeMailMessage);
            log.info("{}", "邮件发送成功 -> " + receiveUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("邮件发送失败");
        }

    }

    /**
     * 发送错误信息
     *
     * @param receive
     * @param request
     * @param e
     */
    @Async
    public void sendErrorMail(String receive, HttpServletRequest request, Exception e) {
        try {
            String host = request.getRemoteHost();
            if (StringUtils.equals(host, "127.0.0.1") || StringUtils.equals(host, "localhost") || Objects.isNull(request)) {
                return;
            }
            StackTraceElement[] stackTrace = e.getStackTrace();
            Map<String, String[]> parameterMap = request.getParameterMap();
            String params = CommonUtil.readToString(parameterMap);
            StringBuilder errorMsg = new StringBuilder("请求路径：" + request.getRequestURL() + "<br />请求Token：" + request.getHeader(RequestConstant.TOKEN) +
                    " <br />" + "请求参数:" + params + " <br />" + "异常信息:\n <br />" + e.getClass() + ":" + e.getMessage() + " <br />");
            List<StackTraceElement> stackTraceElements = Arrays.asList(stackTrace);
            stackTraceElements.stream().forEach(o -> errorMsg.append("&nbsp;&nbsp;&nbsp;" + o.getClassName() + "(" + o.getFileName() + ":" + o.getLineNumber() + ")<br /> \n "));
            this.sendMail(null, receive, errorMsg.toString());
        } catch (Exception ex) {
        }

    }

    @Async
    public void sendTaskErrorMail(String receive, Exception e, String title, String content) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder errorMsg = new StringBuilder();
        if (StringUtils.isNotBlank(content)) {
            errorMsg.append("业务信息：\n " + content + "\n ");
        }
        errorMsg.append("\n异常信息:\n <br />" + e.getClass() + ":" + e.getMessage() + " <br />");
        List<StackTraceElement> stackTraceElements = Arrays.asList(stackTrace);
        stackTraceElements.forEach(o -> errorMsg.append("&nbsp;&nbsp;at&nbsp;" + o.getClassName() + "(" + o.getFileName() + ":" + o.getLineNumber() + ")<br /> \n "));
        this.sendMail(title, receive, errorMsg.toString());
    }


}
