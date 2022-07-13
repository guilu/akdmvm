package com.dbhstudios.akdmvm.util;

import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;

@Log4j2
public class HttpUtils {

    /**
     * Extract Client IP from HttpServletRequest or from X-Forwarded-For header.
     * @param request
     * @return
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = null;

        if (request != null) {
            log.debug("Recibida request: {}", request.getRemoteAddr());
        }


        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            ip = xfHeader.split(",")[0];
        } else {
            ip = request.getRemoteAddr();
        }

        return ip;
    }


    /**
     * Extract uri from httpServletRequest
     *
     * @param request
     * @return String with 'http://server:port/content'
     */
    public static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
