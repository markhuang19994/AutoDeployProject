package com.xxxcomp.tw.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/3, MarkHuang,new
 * </ul>
 * @since 2018/2/3
 */
@Configuration
@ComponentScan(basePackages = "com.xxxcomp.tw.build")
@PropertySource(value = "classpath:config.properties",ignoreResourceNotFound = true)
public class JavaConfig {

}
