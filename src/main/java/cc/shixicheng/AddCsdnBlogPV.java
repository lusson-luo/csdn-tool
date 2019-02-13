package cc.shixicheng;

import cc.shixicheng.util.HttpUtil;
import cc.shixicheng.util.StreamUtil;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 逝夕诚 刷csdn博客访问量
 * 作废了，不知道csdn加了什么屏蔽策略，伪造过ip不起效果
 */
@Deprecated
public class AddCsdnBlogPV {

    private String csdnBlogUrl = "http://blog.csdn.net/";
    private String firstBlogListPageUrl = "http://blog.csdn.net/luo4105"; // 博客主页
    private String nextPagePanner = "<a href=\"/luo4105/article/list/[0-9]{1,10}\">下一页</a>"; // 下一页的正则表达式
    private String nextPageUrlPanner = "/luo4105/article/list/[0-9]{1,10}"; // 下一页Url的正则表达式
    private String artlUrl = "/luo4105/article/details/[0-9]{8,8}"; // 博客utl的正则表达式

    private Set<String> blogListPageUrls = new TreeSet<>();
    private Set<String> blogUrls = new TreeSet<>();

    private static final ExecutorService pool = Executors
            .newFixedThreadPool(2);

    public static void main(String[] args) throws IOException {
        AddCsdnBlogPV pv = new AddCsdnBlogPV();
        pv.visitBlog();
    }

    public void visitBlog() throws IOException {
        long start = System.currentTimeMillis();
        addBlogUrl();
        for (int i = 0; i < 2; i++) {
            int j = 0;
            for (String blogUrl : blogUrls) {
                j++;
                if (j < 85) {
                    continue;
                }
                pool.execute(() -> {
                    try {
                        String artlUrl = csdnBlogUrl + blogUrl;
                        InputStream is = HttpUtil.doGet(artlUrl);
                        if (is != null) {
                            System.out.println(artlUrl + "访问成功");
                        }
                        is.close();
                    } catch (Exception e) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                });
            }
        }
        long end = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        System.out.println(sdf.format(new Date(end - start)));
    }

    /**
     * @throws IOException 加载所有的bolg地址
     */
    public void addBlogUrl() throws IOException {
        String listUrls = "https://blog.csdn.net/luo4105/article/list/";
        int pageCount = getBlogListPageCount();
        for (int i =1;i<pageCount;i++) {
            addBlogUrl(listUrls+i, blogUrls);
        }
    }


    public int getBlogListPageCount() {
        return 20;
    }

    /**
     * 添加搜索博客目录的博客链接
     *
     * @param blogListURL 博客目录地址
     * @param artlUrls    存放博客访问地址的集合
     * @throws IOException
     */
    public void addBlogUrl(String blogListURL, Set<String> artlUrls) throws IOException {
        InputStream is = HttpUtil.doGet(blogListURL);
        String pageStr = StreamUtil.inputStreamToString(is, "UTF-8");
        is.close();
        Pattern pattern = Pattern.compile(artlUrl);
        Matcher matcher = pattern.matcher(pageStr);
        while (matcher.find()) {
            String e = matcher.group(0);
            System.out.println("成功添加博客地址：" + e);
            artlUrls.add(e);
        }
    }

}
