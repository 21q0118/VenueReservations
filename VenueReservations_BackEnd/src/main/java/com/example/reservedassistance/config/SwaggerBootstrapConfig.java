package com.example.reservedassistance.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2
 *
 * @Author maoshufeng
 * @Date 2020-03-30 16:12
 * @Version 1.0
 */
@Slf4j
@Component
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerBootstrapConfig implements WebMvcConfigurer {



    @Bean
    public Docket dashboardDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("大屏")
                .version("1.0.0")
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("大屏")
                .apiInfo(apiInfo)
                .useDefaultResponseMessages(false)

                .select()
                // 包扫描路径
                .apis(RequestHandlerSelectors.basePackage("com.example.reservedassistance"))
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
