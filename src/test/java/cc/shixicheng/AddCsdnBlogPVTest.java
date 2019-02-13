package cc.shixicheng;

import cc.shixicheng.util.HttpUtil;
import cc.shixicheng.util.StreamUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class AddCsdnBlogPVTest {

    AddCsdnBlogPV pv = new AddCsdnBlogPV();

    @Test
    public void visitOnePage() {
        String url = "https://blog.csdn.net/pjw80921/article/details/84541616";
        try {
            for (int i = 0; i < 20; i++) {
                InputStream stream = HttpUtil.doGet(url);
                String connect = StreamUtil.inputStreamToString(stream, "UTF-8");
                stream.close();
                System.out.println(i);
//                Thread.sleep(5000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void addBlogUrl() throws IOException {
        Set<String> artUrls = new HashSet<>();
        pv.addBlogUrl();
    }

    @Test
    public void testVisit() {
        String url = "http://localhost:8080/court/mobile/test";
        InputStream stream = null;
        try {
            stream = HttpUtil.doGet(url);
            String content = StreamUtil.inputStreamToString(stream, "UTF-8");
            stream.close();
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}