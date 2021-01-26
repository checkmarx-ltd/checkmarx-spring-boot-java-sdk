package com.checkmarx.sdk;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Enable Spring Boot autoconfiguration
 *
 * @author ken.mcdonald@checkmarx.com
 */
@SpringBootApplication(scanBasePackages = { "com.checkmarx.sdk", "com.checkmarx.sdk.utils.client"})
public class CheckmarxSdkApplication {


}
