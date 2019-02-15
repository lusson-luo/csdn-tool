package cc.shixicheng;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class GetUsersAndAddArticleTest {

    GetUsersAndAddArticle getUsersAndAddArticle = new GetUsersAndAddArticle();

    @Test
    public void followAndAddLike() {
        getUsersAndAddArticle.followAndAddLike("xx", "xx");
    }

    @Test
    public void getFirstArticleId() {
        Set<String> userNames = new HashSet<>();
        userNames.add("luo4105");
        Map<String, String> articIds = getUsersAndAddArticle.getFirstArticleId(userNames);
        assert articIds != null && articIds.size() == 1 && "86599896".equals(articIds.get("luo4105"));
    }

    @Test
    public void follow() {
        getUsersAndAddArticle.follow("qq_35170267", "ss", "luo4105");
    }

    @Test
    public void charToString() throws UnsupportedEncodingException {
        System.out.println(new String("\u5df2\u5173\u6ce8".getBytes("utf-8"), "utf-8"));
    }

    @Test
    public void mePage() {
        System.out.println(getUsersAndAddArticle.mePage("high2011"));
    }

    @Test
    public void parseFans() {
        Set<String> fans = getUsersAndAddArticle.parseFans(getUsersAndAddArticle.mePage("high2011"));
        System.out.println(fans);
    }

    @Test
    public void getCSDNUsers() {
        String username = "high2011";
        getUsersAndAddArticle.getCSDNUsers(username);
        getUsersAndAddArticle.users.stream().forEach(user -> System.out.println(user));
    }

    @Test
    public void addLike() {
        getUsersAndAddArticle.addLike("lmengi000", "78113257", "", "");
    }

    @Test
    public void login() {
        GetUsersAndAddArticle.UserToken userToken = getUsersAndAddArticle.login("xxx", "xxx");
        assert userToken.getToken() != null && userToken.getToken().length() > 0 && userToken.getUserName().equals(
                "luo4105");
    }


}