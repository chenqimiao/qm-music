package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for FileUtils
 * @author Qimiao Chen
 */
public class FileUtilsTest {

    @Test
    public void testReplaceFileExtension() {
        // Test normal case: replace mp3 with .lrc
        String result1 = FileUtils.replaceFileExtension("/music/song.mp3", ".lrc");
        Assert.assertEquals("/music/song.lrc", result1);
        
        // Test case with extension in path: should only replace the final extension
        String result2 = FileUtils.replaceFileExtension("/music/mp3/song.mp3", ".lrc");
        Assert.assertEquals("/music/mp3/song.lrc", result2);
        
        // Test case with multiple dots in filename
        String result3 = FileUtils.replaceFileExtension("/music/my.favorite.song.mp3", ".lrc");
        Assert.assertEquals("/music/my.favorite.song.lrc", result3);
        
        // Test case without extension
        String result4 = FileUtils.replaceFileExtension("/music/song", ".lrc");
        Assert.assertEquals("/music/song.lrc", result4);
        
        // Test with flac extension
        String result5 = FileUtils.replaceFileExtension("/home/user/music/track.flac", ".lrc");
        Assert.assertEquals("/home/user/music/track.lrc", result5);
    }
}
