package com.liuying.jetpacket.http;

import java.util.ArrayList;
import java.util.List;

public class RetryPathConstants {
    private static final List<String> sRetryPathLIst = new ArrayList<String>() {
        {
            add("/info-news/info/qryHomeRecommend");
        }
    };

    public static boolean matching(String encodedPath) {
        return sRetryPathLIst.contains(encodedPath);
    }
}
