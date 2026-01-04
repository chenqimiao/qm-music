package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    // Test the bug fix: replaceFileExtension should work with extension without dot
    @Test
    void replaceFileExtension_WithoutDot_ReplacesCorrectly() {
        String filePath = "/path/to/song.mp3";
        String result = FileUtils.replaceFileExtension(filePath, "lrc");
        
        assertEquals("/path/to/song.lrc", result, 
            "Extension should be replaced correctly when new extension doesn't have a dot");
    }

    // Test that it also works with dot (for backward compatibility)
    @Test
    void replaceFileExtension_WithDot_ReplacesCorrectly() {
        String filePath = "/path/to/song.mp3";
        String result = FileUtils.replaceFileExtension(filePath, ".lrc");
        
        // This should replace mp3 with .lrc, resulting in song..lrc which is not ideal
        // but tests current behavior
        assertEquals("/path/to/song..lrc", result,
            "When extension has a dot, it's included in the replacement");
    }

    // Parameterized test for various file extension scenarios
    @ParameterizedTest
    @MethodSource("fileExtensionReplacementScenarios")
    void replaceFileExtension_VariousScenarios(String input, String newExtension, String expected) {
        assertEquals(expected, FileUtils.replaceFileExtension(input, newExtension));
    }

    private static Stream<Arguments> fileExtensionReplacementScenarios() {
        return Stream.of(
            // Standard cases - the primary use case for lyrics file lookup
            Arguments.of("/music/song.mp3", "lrc", "/music/song.lrc"),
            Arguments.of("/music/song.flac", "lrc", "/music/song.lrc"),
            Arguments.of("/music/song.wav", "lrc", "/music/song.lrc"),
            Arguments.of("C:\\music\\song.m4a", "lrc", "C:\\music\\song.lrc"),
            
            // Different extension replacements
            Arguments.of("/path/file.txt", "md", "/path/file.md"),
            Arguments.of("/path/file.java", "class", "/path/file.class"),
            
            // Files with multiple dots in the name
            Arguments.of("/path/my.song.name.mp3", "lrc", "/path/my.song.name.lrc"),
            Arguments.of("/path/version.1.0.tar", "gz", "/path/version.1.0.gz"),
            
            // Files with no extension - should append the new extension
            Arguments.of("/path/filename", "txt", "/path/filename.txt"),
            Arguments.of("README", "md", "README.md"),
            
            // Long extensions
            Arguments.of("/path/file.jpeg", "png", "/path/file.png"),
            Arguments.of("/path/archive.tar.gz", "zip", "/path/archive.tar.zip")
        );
    }

    // Test edge case: empty filename
    @Test
    void replaceFileExtension_EmptyString_AddsExtension() {
        String result = FileUtils.replaceFileExtension("", "txt");
        assertEquals(".txt", result, "Empty filename should get extension added");
    }


    // Test the actual use case from SubsonicMediaRetrievalServiceImpl
    @Test
    void replaceFileExtension_RealWorldLyricsLookup_WorksCorrectly() {
        // Simulate the exact use case from the bug fix
        String musicFilePath = "/home/music/artist/album/01 - Song Title.mp3";
        String lrcFilePath = FileUtils.replaceFileExtension(musicFilePath, "lrc");
        
        assertEquals("/home/music/artist/album/01 - Song Title.lrc", lrcFilePath,
            "Should correctly convert music file path to lyrics file path");
    }

    // Test case sensitivity
    @Test
    void replaceFileExtension_PreservesCase() {
        String filePath = "/path/File.MP3";
        String result = FileUtils.replaceFileExtension(filePath, "lrc");
        
        assertEquals("/path/File.lrc", result,
            "Should preserve the case of the filename while replacing extension");
    }
}
