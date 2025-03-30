package com.github.chenqimiao.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 14:18
 **/
@Configuration
public class ModelMapperConfig {


    private Converter<Date, String> dateToStringConverter = new AbstractConverter<Date, String>() {
        @Override
        protected String convert(Date date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return date == null ? null : simpleDateFormat.format(date);
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
