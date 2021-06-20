package com.es.phoneshop.web;

import com.es.phoneshop.security.DefaultDosProtectionService;
import com.es.phoneshop.security.DosProtectionService;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DosFilter implements Filter {
    private DosProtectionService dosProtectionService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosProtectionService = DefaultDosProtectionService.getInstance();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(dosProtectionService.isAllowed(servletRequest.getRemoteAddr())){
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            ((HttpServletResponse)servletRequest).setStatus(429);
        }
    }

    @Override
    public void destroy() {

    }
}
