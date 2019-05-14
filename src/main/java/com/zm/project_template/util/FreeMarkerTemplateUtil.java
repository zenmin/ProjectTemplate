package com.zhuoan.codeapi.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.*;
import java.util.Map;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2018/7/7 16:02
 */
public class FreeMarkerTemplateUtil {

    private FreeMarkerTemplateUtil() {
    }

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        try {
            //这里比较重要，用来指定加载模板所在的路径
            CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/ftl/contract"));
//            CONFIGURATION.setTemplateLoader(new FileTemplateLoader(new File("C:\\Users\\74170\\Desktop")));
            CONFIGURATION.setDefaultEncoding("UTF-8");
            CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }

    public static void generate(final String templateName, File file, Map<String, Object> dataMap) {
        Writer out = null;
        FileOutputStream fos = null;
        try {
            Template template = getTemplate(templateName);
            fos = new FileOutputStream(file);
            out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
            template.process(dataMap, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
