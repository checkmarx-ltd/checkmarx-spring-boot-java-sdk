package com.checkmarx.sdk;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.service.CxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CheckmarxSdkApplicationTests {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(CheckmarxSdkApplicationTests.class);

	@Autowired
	private CxProperties cxProperties;

	@Test
	public void testCxProperties(){
		log.info(cxProperties.getBaseUrl());
		assertEquals("/CxServer/SP/America/cxts", cxProperties.getTeam());
	}

}
