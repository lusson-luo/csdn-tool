package cc.shixicheng;

import cc.shixicheng.util.HttpUtil;
import cc.shixicheng.util.StreamUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;

public class PullCsdnUsers {

    public static final String DEFAULT_USER = "luo4105";
    public Set<String> users = new HashSet<>();

    public void pullUsers(int maxSize) {
        Set<UserInfo> userInfos = readUsersFile();
        if (userInfos == null || userInfos.size() == 0) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(DEFAULT_USER);
            userInfos.add(userInfo);
        }
        Set<UserInfo> parseUserInfos = parseUserInfos(userInfos, maxSize);
        userInfos.addAll(parseUserInfos);
        saveUsers(userInfos);
    }

    public Set<UserInfo> readUsersFile() {
        Set<UserInfo> userInfos = new HashSet<>();
        String filePath = "/Users/marx_luo/Documents/csdn-tool.wiki/userInfo.json";
        File file = new File(filePath);
        try {
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                String userInfo = StreamUtil.inputStreamToString(inputStream, "UTF-8");
                JSONArray array = JSON.parseArray(userInfo);
                List<UserInfo> readUsers = array.toJavaList(UserInfo.class);
                if (readUsers != null && readUsers.size() != 0) {
                    userInfos.addAll(readUsers);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("成功读取" + userInfos.size() + "条数据");
        }
        return userInfos;
    }

    public Set<UserInfo> parseUserInfos(Set<UserInfo> userInfos, int maxSize) {
        if (userInfos != null && userInfos.size() != 0) {
            ArrayList<UserInfo> userInfoList = new ArrayList<>();
            userInfoList.addAll(userInfos);
            for (int i = userInfoList.size() - 1; i >= 0; i--) {
                getCSDNUsers(userInfoList.get(i).getUserName(), maxSize);
            }
        } else {
            System.out.println("没有用户数据提供追踪");
        }
        Set<UserInfo> parseUsers = users.stream().map(userName -> {
            Set<UserInfo> exitUser =
                    userInfos.stream().filter(userInfo -> userInfo.getUserName().equals(userName)).collect(Collectors.toSet());
            if (exitUser == null || exitUser.size() == 0) {
                return parseUserInfos(userName);
            }
            return null;
        }).filter(userInfo -> userInfo != null).collect(Collectors.toSet());
        return parseUsers.stream().filter(parseUser -> parseUser != null).collect(Collectors.toSet());
    }

    public UserInfo parseUserInfos(String userName) {
        String mePage = mePage(userName);
        if (mePage == null || mePage.length() == 0) {
            return null;
        }
        mePage = mePage.replaceAll(" ", "");
        String userInfoRegex = "<li>\n<span>([0-9]*)</span>\n<strong>访问</strong>\n</li>\n" +
                "<li>\n<span>([0-9]*)</span>\n<ahref=\".*\"><strong>原创</strong></a>\n</li>\n" +
                "<li>\n<span>[0-9]*</span>\n<strong>转发</strong>\n</li>\n" +
                "<li>\n<span>[0-9]*</span>\n<strong>排名</strong>\n</li>\n" +
                "<li>\n<span>([0-9]*)</span>\n<strong>评论</strong>\n</li>\n" +
                "<li>\n<span>[0-9]*</span>\n<strong>获赞</strong>\n</li>";
        Pattern pattern = Pattern.compile(userInfoRegex);
        Matcher matcher = pattern.matcher(mePage);
        if (matcher.find()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setPageView(matcher.group(1));
            userInfo.setBlogCount(matcher.group(2));
            userInfo.setCommentCount(matcher.group(3));
            System.out.println("成功获得用户" + userName + "信息");
            return userInfo;
        }
        return null;
    }


    public void getCSDNUsers(String userName, int maxSize) {
        if (users.size() > maxSize) {
            return;
        }
        String mePage = mePage(userName);
        Set<String> userList = parseFans(mePage);
        if (userList != null && userList.size() > 0) {
            synchronized (users) {
                userList = userList.stream().filter(user -> !users.contains(user)).collect(Collectors.toSet());
                users.addAll(userList);
                System.out.println("访问" + userName + "主页，已找到" + users.size() + "个用户");
            }
            userList.stream().forEach(user -> getCSDNUsers(user, maxSize));
        }
    }

    public String mePage(String username) {
        String follerUrl = "https://me.csdn.net/" + username;
        try {
            Map<String, String> headers = new HashMap<>();
            InputStream inputStream = HttpUtil.doGet(follerUrl, headers);
            String response = StreamUtil.inputStreamToString(inputStream, "UTF-8");
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> parseFans(String pageContext) {
        if (pageContext == null || pageContext.length() == 0) {
            return new HashSet<>();
        }
        String fansRegex = "a class=\"fans_title\" href=\"https://me.csdn.net/(.+)\"";
        Pattern pattern = Pattern.compile(fansRegex);
        Matcher matcher = pattern.matcher(pageContext);
        Set<String> fanses = new HashSet<>();
        while (matcher.find()) {
            fanses.add(matcher.group(1));
        }
        return fanses;
    }

    public void saveUsers(Set<UserInfo> userInfos) {
        String userString = JSON.toJSONString(userInfos);
        File file = new File("/Users/marx_luo/Documents/csdn-tool.wiki/userInfo.json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(userString.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            System.out.println("成功写入" + userInfos.size() + "用户信息，写入地址：" + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class UserInfo {
        @JSONField(name = "user_name")
        private String userName;
        @JSONField(name = "page_view")
        private String pageView;
        @JSONField(name = "comment_count")
        private String commentCount;
        @JSONField(name = "blog_count")
        private String blogCount;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PullCsdnUsers.UserInfo userInfo = (PullCsdnUsers.UserInfo) o;
            return Objects.equals(userName, userInfo.userName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userName);
        }
    }
}
