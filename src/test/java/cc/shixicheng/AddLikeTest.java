package cc.shixicheng;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddLikeTest {

    AddLike addLike = new AddLike();

    @Test
    public void addLike() {
        addLike.addLike("xx", "xx");
    }

    @Test
    public void articleAddLike() {
        addLike.articleAddLike("c94444", "76945088", "ee4ebae65d714934bae27c2b7b06d5cc", "luo4105");
    }

    @Test
    public void login() {
        AddLike.UserToken userToken = addLike.login("xx", "xx");
        assert userToken.getToken() != null && userToken.getToken().length() > 0 && userToken.getUserName().equals(
                "luo4105");
    }
}