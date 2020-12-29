package com.jin10.pushsocket.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;

@SuppressWarnings("deprecation")
public class JsonUtils2 {
	private static ObjectMapper SAFE_MAPPER = new ObjectMapper();
	
	static {

		//SAFE_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//SAFE_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		SAFE_MAPPER.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		SAFE_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		//SAFE_MAPPER.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
	}

	public static ObjectMapper createMapper()
	{
		return SAFE_MAPPER;
	}
	
	public static <T> T readValue(String value, Class<T> clazz) {
		try
		{
			return SAFE_MAPPER.readValue(value, clazz);
		}
		catch (Exception ex) {
			throw new RuntimeException("Parse JSON error", ex);
		}
	}
	
	public static String writeValue(Object obj) {
		try
		{
			return SAFE_MAPPER.writeValueAsString(obj);
		}
		catch (Exception ex) {
			throw new RuntimeException("Parse JSON error", ex);
		}
	}



	/**
	 * 格式化
	 *
	 * @param jsonStr
	 * @return
	 * @author lizhgb
	 * @Date 2015-10-14 下午1:17:35
	 * @Modified 2017-04-28 下午8:55:35
	 */
	public static String formatJson(String jsonStr) {
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		boolean isInQuotationMarks = false;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			switch (current) {
				case '"':
					if (last != '\\'){
						isInQuotationMarks = !isInQuotationMarks;
					}
					sb.append(current);
					break;
				case '{':
				case '[':
					sb.append(current);
					if (!isInQuotationMarks) {
						sb.append('\n');
						indent++;
						addIndentBlank(sb, indent);
					}
					break;
				case '}':
				case ']':
					if (!isInQuotationMarks) {
						sb.append('\n');
						indent--;
						addIndentBlank(sb, indent);
					}
					sb.append(current);
					break;
				case ',':
					sb.append(current);
					if (last != '\\' && !isInQuotationMarks) {
						sb.append('\n');
						addIndentBlank(sb, indent);
					}
					break;
				default:
					sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加space
	 *
	 * @param sb
	 * @param indent
	 * @author lizhgb
	 * @Date 2015-10-14 上午10:38:04
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append('\t');
		}
	}





	private static ObjectMapper MAPPER;

	static {
		MAPPER = generateMapper(JsonInclude.Include.ALWAYS);
	}



	public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
		return clazz.equals(String.class) ? (T) json : MAPPER.readValue(json, clazz);
	}


	public static <T> T fromJson(String json, TypeReference<?> typeReference) throws IOException {
		return (T) (typeReference.getType().equals(String.class) ? json : MAPPER.readValue(json, typeReference));
	}


	public static <T> String toJson(T src) throws IOException {
		return src instanceof String ? (String) src : MAPPER.writeValueAsString(src);
	}


	public static <T> String toJson(T src, JsonInclude.Include inclusion) throws IOException {
		if (src instanceof String) {
			return (String) src;
		} else {
			ObjectMapper customMapper = generateMapper(inclusion);
			return customMapper.writeValueAsString(src);
		}
	}


	public static <T> String toJson(T src, ObjectMapper mapper) throws IOException {
		if (null != mapper) {
			if (src instanceof String) {
				return (String) src;
			} else {
				return mapper.writeValueAsString(src);
			}
		} else {
			return null;
		}
	}

//    public static ObjectMapper mapper() {
//        return MAPPER;
//    }


	private static ObjectMapper generateMapper(JsonInclude.Include include) {

		ObjectMapper customMapper = new ObjectMapper();

		// 设置输出时包含属性的风格
		customMapper.setSerializationInclusion(include);

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 禁止使用int代表Enum的order()来反序列化Enum,非常危险
		customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		// 所有日期格式都统一为以下样式
		customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return customMapper;
	}
}
