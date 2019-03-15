package com.zm.project_template.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class JSONUtil {

	private JSONUtil() {
	}

	private static final ObjectMapper m = new ObjectMapper();

	public static String toJSONString(Object o) {
		try {
			m.setSerializationInclusion(Include.NON_EMPTY);
			return m.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public static <T> T parseObject(String jsonString, Class<T> elementClasses) {
		m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			return m.readValue(jsonString, elementClasses);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> List<T> parseArray(String jsonString, Class<T> elementClasses) {
		m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		JavaType javaType = m.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
		try {
			return m.readValue(jsonString, javaType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
