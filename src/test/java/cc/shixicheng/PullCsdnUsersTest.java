package cc.shixicheng;

import org.junit.Test;

import static org.junit.Assert.*;

public class PullCsdnUsersTest {

    PullCsdnUsers pullCsdnUsers = new PullCsdnUsers();

    @Test
    public void pullUsers() {
        pullCsdnUsers.pullUsers(500);
    }

    @Test
    public void readUsersFile() {
    }

    @Test
    public void parseUserInfo() {
    }

    @Test
    public void parseUserInfo1() {
        assert pullCsdnUsers.parseUserInfo("laihongming")!=null;
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