package com.github.chenqimiao.config;

import com.github.chenqimiao.util.DateTimeUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 14:18
 **/
@Configuration
public class ModelMapperConfig {


    private final Converter<Date, String> dateToStringConverter = new AbstractConverter<>() {
        @Override
        protected String convert(Date date) {

            return date == null ? null : DateTimeUtils.format(date, DateTimeUtils.YMDHMS);
        }
    };


    private final Converter<Long, Date> longToDateConverter = new AbstractConverter<>() {
        @Override
        protected Date convert(Long timestamp) {
            if (timestamp == null) {
                return null;
            }
            return new Date(timestamp);
        }
    };

    private final Converter<Timestamp, Long> timestampToLongConverter = new AbstractConverter<>() {
        @Override
        protected Long convert(Timestamp timestamp) {
            if (timestamp == null) {
                return null;
            }
            return timestamp.getTime();
        }
    };


    public static class NullSafeModelMapper extends ModelMapper {

        public <D> D map(Object source, Class<D> destinationType) {
            if (source == null) return null;
            return super.map(source, destinationType);
        }

        public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
            if (source == null) return null;
            return super.map(source, destinationType, typeMapName);
        }

        public void map(Object source, Object destination) {
            if (source == null) return;
            super.map(source, destination);
        }

        public void map(Object source, Object destination, String typeMapName) {
            if (source == null) return;
            super.map(source, destination, typeMapName);
        }

        public <D> D map(Object source, Type destinationType) {
            if (source == null) return null;

            return super.map(source, destinationType);
        }

        public <D> D map(Object source, Type destinationType, String typeMapName) {
           if (source == null) return null;
           return super.map(source, destinationType, typeMapName);
        }
    }

    @Bean
    @Primary
    public ModelMapper modelMapper() {


        ModelMapper modelMapper = new NullSafeModelMapper();

        // @see http://modelmapper.org/user-manual/configuration/

        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);

        // 匹配策略使用严格模式
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(dateToStringConverter);

        modelMapper.addConverter(longToDateConverter);

        return modelMapper;
    }


    @Bean
    public ModelMapper ucModelMapper() {


        ModelMapper modelMapper = new NullSafeModelMapper();

        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);

        // 匹配策略使用严格模式
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
                .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

        modelMapper.addConverter(timestampToLongConverter);

        return modelMapper;
    }

    @Bean
    public ModelMapper cuModelMapper() {


        ModelMapper modelMapper = new NullSafeModelMapper();

        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);

        // 匹配策略使用严格模式
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);

        modelMapper.addConverter(timestampToLongConverter);
        return modelMapper;
    }

}
