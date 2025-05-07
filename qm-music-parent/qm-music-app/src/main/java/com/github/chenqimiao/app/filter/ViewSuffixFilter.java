package com.github.chenqimiao.app.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 00:43
 **/
public class ViewSuffixFilter extends OncePerRequestFilter {

    private static final String SUFFIX = ".view";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.endsWith(SUFFIX)) {
            // 移除.view后缀并转发
            String newPath = path.substring(0, path.length() - SUFFIX.length());
            request.getRequestDispatcher(newPath).forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
