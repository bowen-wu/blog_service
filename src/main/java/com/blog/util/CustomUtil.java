package com.blog.util;

import org.springframework.util.StringUtils;

public class CustomUtil {
    public static String getDescription(String description, String content) {
        if (StringUtils.hasText(description)) {
            return description;
        }
        if (content.length() <= 20) {
            return content;
        }
        return content.substring(0, 20);
    }
}
