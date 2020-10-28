//package qunar.tc.bistoury.proxy.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.ModelAndViewDefiningException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//import java.util.Map;
//
////@Configuration
//public class SuffixPatternInterceptor implements HandlerInterceptor, ApplicationContextAware {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String path = request.getRequestURI();
//        if (path.endsWith(".do")) {
//            path = path.substring(0, path.length() - 3);
//            //request.getRequestDispatcher(path).forward(request, response);
////            response.sendRedirect(path);
//            Map<String, Object> map = new HashMap() {{
//                put("context", request.getAttribute("context"));
//            }};
//            ModelAndView mav = new ModelAndView(path, map);
//            mav.addObject("message", path);
//
//            throw new ModelAndViewDefiningException(mav);
//        }
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//
//    }
//}
