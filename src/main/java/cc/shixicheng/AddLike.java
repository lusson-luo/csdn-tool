package cc.shixicheng;

import cc.shixicheng.util.HttpUtil;
import cc.shixicheng.util.StreamUtil;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;

public class AddLike {

    PullCsdnUsers pullCsdnUsers = new PullCsdnUsers();

    public void addLike(String userNo, String password) {
        UserToken token = login(userNo, password);
        if (token != null && token.getToken() != null) {
            Set<PullCsdnUsers.UserInfo> userInfos = pullCsdnUsers.readUsersFile();
            Map<String, String> firstArticleIds =
                    getFirstArticleId(userInfos.stream().filter(userInfo -> userInfo.getBlogCount() != 0).map(PullCsdnUsers.UserInfo::getUserName).collect(Collectors.toSet()));
            firstArticleIds.keySet().stream().forEach(userName ->
                    articleAddLike(userName, firstArticleIds.get(userName), token.getToken(), token.userName));
        }
    }

    /**
     * @desc 分析得知，点赞采用get方式请求，接口https://blog.csdn.net/?/phoenix/article/digg?ArticleId=?，第一个?是用户名，第二个?是文章id
     * 。这个接口及时点赞，也是取消点赞，它两返回值返回值不同
     * 点赞{"status":true,"digg":2,"bury":"0"}
     * 取消点赞{"status":true,"digg":1,"bury":"0"}
     */
    public void articleAddLike(String userName, String articleId, String token, String myUserNo) {
        String url = "https://blog.csdn.net/" + userName + "/phoenix/article/digg?ArticleId=" + articleId;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("cookie", "UserName=" + myUserNo + "; " +
                    "UserToken=" + token);
            InputStream inputStream = HttpUtil.doGet(url, headers);
            String response = StreamUtil.inputStreamToString(inputStream, "UTF-8");
            inputStream.close();
            CsdnAddLikeResponse addLikeResponse = JSONObject.parseObject(response,CsdnAddLikeResponse.class);
            if (addLikeResponse.getDigg().equals(0)) {
                articleAddLike(userName, articleId, token, myUserNo);
            }
            System.out.println("点赞成功:" + "https://blog.csdn.net/" + userName + "/article/details/" + articleId);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("点赞失败:" + "https://blog.csdn.net/" + userName + "/article/details/" + articleId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("点赞失败:" + "https://blog.csdn.net/" + userName + "/article/details/" + articleId);
        }
    }

    public UserToken login(String username, String password) {
        String url = "https://passport.csdn.net/v1/register/pc/login/doLogin";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("accept", "application/json;charset=UTF-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loginType", "1");
        jsonObject.put("pwdOrVerifyCode", password);
        jsonObject.put("userIdentification", username);
        UserToken userToken = new UserToken();
        try {
            Map<String, List<String>> repHeaders = HttpUtil.doPostJSON(url, headers,
                    jsonObject.toJSONString());
            List<String> setCookies = repHeaders.get("Set-Cookie");
            if (setCookies != null && setCookies.size() > 0) {
                setCookies.stream().forEach(cook -> {
                    if (cook.contains("UserToken=")) {
                        String userTokenRegex = "UserToken=(.*); Max";
                        Pattern pattern = Pattern.compile(userTokenRegex);
                        Matcher matcher = pattern.matcher(cook);
                        while (matcher.find()) {
                            userToken.setToken(matcher.group(1));
                        }
                    }
                    if (cook.contains("UserName=")) {
                        String userTokenRegex = "UserName=(.*); Max";
                        Pattern pattern = Pattern.compile(userTokenRegex);
                        Matcher matcher = pattern.matcher(cook);
                        while (matcher.find()) {
                            userToken.setUserName(matcher.group(1));
                        }
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userToken.getToken() != null && !userToken.getToken().equals("")) {
            System.out.println("login success, token is " + userToken.getToken());
        } else {
            System.out.println("login failed, userNo is " + userToken.getUserName());
        }
        return userToken;
    }

    /**
     * @return key:userName,value:articleId
     */
    public Map<String, String> getFirstArticleId(Set<String> userNames) {
        Map<String, String> articleIdMap = new HashMap<>();
        String blogUrlTemplate = "https://blog.csdn.net/%s/article/list/1";
        userNames.stream().forEach(userName -> {
            String blogUrl = String.format(blogUrlTemplate, userName);
            try {
                InputStream inputStream = HttpUtil.doGet(blogUrl);
                String content = StreamUtil.inputStreamToString(inputStream, "UTF-8");
                String articleIdRegex = "https://blog.csdn.net/" + userName + "/article/details/([0-9]{8})";
                Pattern pattern = Pattern.compile(articleIdRegex);
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    articleIdMap.put(userName, matcher.group(1));
                    break;
                }
            } catch (IOException e) {
                System.out.println("http get failed");
                e.printStackTrace();
            }
        });
        return articleIdMap;
    }

    @Data
    public static class UserToken {
        private String token;
        private String userName;
    }

    @Data
    public static class CsdnAddLikeResponse {
        private boolean status;
        private Integer digg;
        private String bury;
    }
}
