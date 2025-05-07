package com.github.chenqimiao.service;

import com.github.chenqimiao.app.QmMusicApplication;
import com.github.chenqimiao.core.constant.CommonConstants;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/8 15:53
 **/
@SpringBootTest(classes = QmMusicApplication.class)
@Rollback
public class ArtistNameTest {

    @Test
    void artistNameTest() {
        String artistNameDelimiterRegx = CommonConstants.ARTIST_NAME_DELIMITER_REGX;

        List<String> originalArtistNames = Lists.newArrayList("pp and cqm"
                , "pp & cqm", "pp , cqm", "pp 、 cqm", "pp ， cqm", "pp ； cqm"
                , "pp \\ cqm", "pp / cqm", "pp/cqm"
                 );

        originalArtistNames.forEach(originalArtistName -> {
            String[] split = originalArtistName.split(artistNameDelimiterRegx);
            Assert.assertEquals(originalArtistName, split.length, 2 );
            Assert.assertEquals(originalArtistName, split[0].trim(), "pp");
            Assert.assertEquals(originalArtistName, split[1].trim(), "cqm" );

        });
    }
}
