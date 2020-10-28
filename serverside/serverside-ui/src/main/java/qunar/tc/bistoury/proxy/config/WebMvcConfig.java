//package qunar.tc.bistoury.proxy.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
////@Configuration
////@ComponentScan
////@EnableWebMvc
//public class WebMvcConfig implements WebMvcConfigurer {
//
//
//    @Autowired
//    SuffixPatternInterceptor suffixPatternInterceptor;
//
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/",
//                "classpath:/static/", "classpath:/public/");
////        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(suffixPatternInterceptor).addPathPatterns("/**");
//    }
//
//
//}
