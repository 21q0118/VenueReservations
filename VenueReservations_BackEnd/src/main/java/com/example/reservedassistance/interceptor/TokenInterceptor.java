package com.example.reservedassistance.interceptor;

import com.example.reservedassistance.utils.TokenUtil;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final Integer SUPER_MANAGER_ID = 16;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("token");
        if(token != null){
            boolean result = TokenUtil.verify(token);
            if(result){

                int id = TokenUtil.getId(token);
                String role = TokenUtil.getRole(token);
                String userId = request.getParameter("userId");
                String managerId = request.getParameter("managerId");
                if(userId != null){
                    if(id != Integer.parseInt(userId) || !role.equals("用户")) {
                        System.out.println("身份错误");
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=utf-8");
                        JSONObject json = new JSONObject();
                        json.put("msg","token verify fail");
                        json.put("code","50000");
                        response.getWriter().append(json.toJSONString());
                        return false;
                    }
                }

                if(managerId != null){
                    if(id != Integer.parseInt(managerId)) {
                        System.out.println("身份错误");
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=utf-8");
                        JSONObject json = new JSONObject();
                        json.put("msg","token verify fail");
                        json.put("code","50000");
                        response.getWriter().append(json.toJSONString());
                        return false;
                    }
                    if((id == SUPER_MANAGER_ID && role.equals("超级管理员"))
                            || (id != SUPER_MANAGER_ID && role.equals("场馆管理员")) )
                        return true;
                    System.out.println("身份错误");
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    JSONObject json = new JSONObject();
                    json.put("msg","token verify fail");
                    json.put("code","50000");
                    response.getWriter().append(json.toJSONString());
                    return false;
                }

                System.out.println("通过拦截器");
                return true;
            }
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        json.put("msg","token verify fail");
        json.put("code","50000");
        response.getWriter().append(json.toJSONString());
        System.out.println("认证失败，未通过拦截器");
        System.out.println(token);
        return true;
    }

}
