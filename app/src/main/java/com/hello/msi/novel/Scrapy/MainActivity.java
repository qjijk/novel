package com.hello.msi.novel.Scrapy;

/**
 * Created by msi on 2018/3/15.
 *
 */

import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.Toast;

import com.hello.msi.novel.Data.News;
import com.hello.msi.novel.List.NovelItem;
import com.hello.msi.novel.R;
import com.hello.msi.novel.Translate.TextMain;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.listener.RefreshHandler;

public class MainActivity extends AppCompatActivity {

    private List<News> newsList;
    private NovelItem adapter;
    private Handler handler;
    private ListView lv;
    private QRefreshLayout qRefreshLayout;
    private int h = 1;
    private int a = 1;
    private Elements titleLinks;

    private String urls = "https://www.webnovel.com/apiajax/category/ajax?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&orderBy=4&pageIndex="+h+"=&category=0&tagName=&bookType=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsList = new ArrayList<>();
        getNews(a);
        run();
        initListener();

    }
/**
 *
 */


    public void run()
    {

        lv = (ListView) findViewById(R.id.news_lv);//获取布局文件中的ListView控件对象(命名为lv)
        qRefreshLayout = (QRefreshLayout) findViewById(R.id.refreshlayouts);
        qRefreshLayout.setLoadMoreEnable(true);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    setclick();
                }
            }
        };
    }

    public void  setclick()
    {
        adapter = new NovelItem(MainActivity.this,newsList);//创建数组适配器对象，并且通过参数设置类item项的布局样式和数据源
        lv.setAdapter(adapter);//把数组适配器加载到lv控件中(ListView)
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//ListView事件监听
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = newsList.get(position);//获取用户点击的位置
                Intent intent = new Intent(MainActivity.this,ChapterActivity.class);//要打开的是ChapterActivity
                intent.putExtra("news_url",news.getNewsUrl());//给ChapterActivity传递网址。命名为new_url
                startActivity(intent);//开始一个新的Activity
            }
        });
    }

    Toast toast;
    public void showToast(String msgs) {
        try{
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), msgs, Toast.LENGTH_SHORT);
            }
            else {
                toast.setText(msgs);
            }
            toast.show();
        }catch (Exception e){
            Looper.prepare();
            Toast.makeText(getApplicationContext(),msgs,Toast.LENGTH_SHORT);
            Looper.loop();
        }


    }
    private void initListener(){
        qRefreshLayout.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
               /* handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNews();
                    }
                },5000);*/
                showToast("无更新");
                qRefreshLayout.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                a++;
                getNews(a);
                qRefreshLayout.loadMoreComplete();

            }
        });
    }


    private void getNews(final int a){


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                        String doc = null;


                        System.out.println(a);

                        URL url = new URL("https://www.webnovel.com/apiajax/category/ajax?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&orderBy=4&pageIndex="+a+"&category=0&tagName=&bookType=0");

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(500);
                        conn.setRequestMethod("GET");
                        int code = conn.getResponseCode();
                        //Log.e("sq", String.valueOf(code));
                        if(code == 200){
                            InputStream inputStream = conn.getInputStream();
                            doc = StreamTool.decodeStream(inputStream);
                            //Log.e("qw", doc);

                        }
                        else {

                        }

                        JsonToXml jsonToXml = new JsonToXml.Builder(doc).build();
                        String xmlString = jsonToXml.toString();
                        //int indentationSize = 6;
                        //String formattedXml = jsonToXml.toFormattedString(indentationSize);
                        //Log.i("xmlString",xmlString);
                        //System.out.println(xmlString);
                        //Log.i("formattedXml",formattedXml);
                        Document docs = Jsoup.parse(xmlString);
                        titleLinks = docs.select("items");

                        for(int j = 0;j < (titleLinks.size());j++){
                            String uri = titleLinks.get(j).select("bookId").text();
                            String id = titleLinks.get(j).select("bookId").text();
                            String title = titleLinks.get(j).select("bookName").text();
                            //Log.e(title,uri);
                            //Document docs = Jsoup.connect("https://m.webnovel.com/"+uri).get();
                            //Elements cName = docs.select("span > a");
                            String cname = titleLinks.get(j).select("authorName").text();
                            cname = "作者：" + cname;
                            //Log.e("cname",cname);
                            //Log.e("id",id);
                            News news = new News(title,uri,cname, id);
                            newsList.add(news);//小说列表+1
                        }

                        //Log.e("sp", String.valueOf(titleLinks));
                        //Log.e("title",Integer.toString(titleLinks.size()));



                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);



                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}


