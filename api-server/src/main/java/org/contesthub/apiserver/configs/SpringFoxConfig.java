//package org.contesthub.apiserver.configs;
//
//import io.swagger.v3.oas.models.info.License;
//import io.swagger.v3.oas.models.ExternalDocumentation;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class SpringFoxConfig {
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.baeldung.swaggerconf.controller"))
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
////    @Bean
////    public OpenAPI awesomeAPI() {
////        return new OpenAPI()
////                .info(new Info().title("Awesome API Title")
////                        .description("Awesome API Description")
////                        .version("1.0")
////                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")))
////                .externalDocs(new ExternalDocumentation()
////                        .description("Ranga Karanam, in28minutes@gmail.com")
////                        .url("http://www.in28minutes.com"));
////    }
//
//
//}
