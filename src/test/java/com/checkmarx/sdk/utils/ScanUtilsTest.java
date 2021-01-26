package com.checkmarx.sdk.utils;

import com.checkmarx.sdk.dto.sast.CxConfig;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ScanUtilsTest {

    @Test
    public void testGetConfigAsCode() {
        File file = new File(
            getClass().getClassLoader().getResource("CxConfig.json").getFile()
        );
        CxConfig config = ScanUtils.getConfigAsCode(file);
        assertNotNull(config);
        assertTrue(config.getActive());
        assertEquals("/a/b/c", config.getTeam());
    }
}
