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
            return new Date(timestamp);
        }
    };

    @Bean
    @Primary
    public ModelMapper modelMapper() {


        ModelMapper modelMapper = new ModelMapper();

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


        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);

        // 匹配策略使用严格模式
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
                .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

        return modelMapper;
    }

    @Bean
    public ModelMapper cuModelMapper() {


        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);

        // 匹配策略使用严格模式
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);

        return modelMapper;
    }

}
