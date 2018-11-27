package org.webserver.common;

import org.apache.commons.lang3.StringUtils;

public class FileUtils {

    public static String getContentType(String fileRequested) {
        if(StringUtils.isBlank(fileRequested))
            throw new IllegalArgumentException("Please pass a valid argument");

        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";
        else if (fileRequested.endsWith(".css"))
            return "text/css";
        else if (fileRequested.toLowerCase().endsWith(".gif"))
            return "image/gif";
        else if (fileRequested.toLowerCase().endsWith(".jpg"))
            return "image/jpeg";
        else
            return "text/plain";
    }
}
