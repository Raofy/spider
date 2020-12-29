package com.jin10.spider.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * @author Airey
 * @date 2020/5/26 10:54
 * ----------------------------------------------
 * 将Date转为long 单位:ms
 * ----------------------------------------------
 */
public class DateToLongSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(date.getTime());
    }
}
