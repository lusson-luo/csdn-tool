package cc.shixicheng;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class PullCsdnUsersTest {

    PullCsdnUsers pullCsdnUsers = new PullCsdnUsers();

    @Test
    public void pullUsers() {
        pullCsdnUsers.pullUsers(50);
    }

    @Test
    public void readUsersFile() {
    }

    @Test
    public void parseUserInfo() {
    }

    @Test
    public void parseUserInfo1() {
        assert pullCsdnUsers.parseUserInfos("laihongming")!=null;
    }

    @Test
    public void getCSDNUsers() {
    }

    @Test
    public void mePage() {
    }

    @Test
    public void parseFans() {
    }

    @Test
    public void saveUsers() {
        Set<PullCsdnUsers.UserInfo> userInfos = new HashSet<>();
        PullCsdnUsers.UserInfo userInfo = new PullCsdnUsers.UserInfo();
        userInfo.setUserName("shixicheng");
        userInfo.setBlogCount(120);
        userInfo.setPageView(23011);
        userInfo.setCommentCount(13);
        userInfos.add(userInfo);
        pullCsdnUsers.saveUsers(userInfos);
    }
}