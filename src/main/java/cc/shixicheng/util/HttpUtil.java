package cc.shixicheng.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HttpUtil {
    public static InputStream doGet(String urlstr) throws IOException {
        return doGet(urlstr, null);
    }

    public static InputStream doGet(String urlstr, Map<String, String> headers) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp," +
                "image/apng,*/*;q=0" +
                ".8");
        if (headers != null) {
            Set<String> keys = headers.keySet();
            keys.stream().forEach(key -> {
                conn.setRequestProperty(key, headers.get(key));
            });
        }
        Random random = new Random();
        String ip =
                (random.nextInt(100) + 100) + "." + (random.nextInt(100) + 100) + "." + (random.nextInt(100) + 100) + "." + (random.nextInt(100) + 100);
        conn.setRequestProperty("x-forwarded-for", ip);
        InputStream inputStream = conn.getInputStream();

        return inputStream;
    }

    public static String doPostForm(String urlstr, Map<String, String> headers,String params) throws IOException {
        headers.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        Map<String, List<String>> response = doPost(urlstr, headers, params);
        if (response != null) {
            List<String> context = response.get("respBody");
            if (context!=null && context.size() > 0) {
                return context.get(0);
            } else {
                return null;
            }
        }
        return null;
    }


    public static Map<String, List<String>> doPostJSON(String urlstr, Map<String, String> headers, String params) throws IOException {
        headers.put("content-type", "application/json;charset=UTF-8");
        headers.put("accept", "application/json, text/plain, */*");
        return doPost(urlstr, headers, params);
    }

    public static Map<String, List<String>> doPost(String urlstr, Map<String, String> headers,
                                                                String params) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        if (headers != null) {
            Set<String> keys = headers.keySet();
            keys.stream().forEach(key -> {
                conn.setRequestProperty(key, headers.get(key));
            });
        }

        conn.connect();

        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(params.getBytes("UTF-8"));
        outputStream.close();

        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            String response = StreamUtil.inputStreamToString(inputStream, "UTF-8");
            inputStream.close();
            conn.disconnect();

            //unmodifiable map
            Map<String, List<String>> unmodifiableResp = conn.getHeaderFields();
            Map<String, List<String>> resps = new HashMap<>();
            unmodifiableResp.keySet().stream().forEach(key -> resps.put(key, unmodifiableResp.get(key)));
            unmodifiableResp.keySet().stream().forEach(key -> System.out.println(key + ":{" + unmodifiableResp.get(key) + "}"));
            resps.put("respBody", Arrays.asList(response));
            return resps;
        } else {
            System.out.println("请求错误：" + conn.getResponseCode());
            InputStream inputStream = conn.getErrorStream();
            String response = StreamUtil.inputStreamToString(inputStream, "UTF-8");
            conn.disconnect();
            System.out.println(response);
            return null;
        }
    }

}