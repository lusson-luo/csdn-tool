package cc.shixicheng;

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
}