package com.xxxcomp.tw;

import com.xxxcomp.tw.build.BuildProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.xxxcomp.tw.spring.JavaConfig;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/1/20, MarkHuang,new
 * </ul>
 * @since 2018/1/20
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JavaConfig.class);
        logger.debug("歡迎使用Mark專案自動建置系統^_^");
        System.out.println(logger.isDebugEnabled());
        ctx.getBean("buildProject", BuildProject.class).build();


    }


}
