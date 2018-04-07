package com.hello.msi.novel.Scrapy;

/**
 * Created by msi on 2018/3/15.
 *
 */

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import com.hello.msi.novel.Data.News;
import com.hello.msi.novel.List.NovelItem;
import com.hello.msi.novel.R;

public class MainActivity extends AppCompatActivity {

    private List<News> newsList;
    private NovelItem adapter;
    private Handler handler;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.news_lv);//获取布局文件中的ListView控件对象(命名为lv)
        getNews();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
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
            }
        };

    }
/**
 *
 */


    private void getNews(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    for(int i = 1;i<=1;i++) {

                        Document doc = Jsoup.connect("https://m.webnovel.com/booklist/category?order=4=3&title=All Genre&pageIndex="+i+"&categoryName=All Genre").get();
                        Elements titleLinks = doc.select("ul.j_bookstoreUl > li > a");    //解析获取标题与链接地址
                        Log.e("title",Integer.toString(titleLinks.size()));
                        for(int j = 0;j < (titleLinks.size());j++){
                            String uri = titleLinks.get(j).select("a").attr("href");
                            String id = titleLinks.get(j).select("a").attr("data-bookid");
                            String title = titleLinks.get(j).select("h3").text();
                            Log.e(title,uri);
                            Document docs = Jsoup.connect("https://m.webnovel.com"+uri).get();
                            Elements cName = docs.select("span > a");
                            String cname = cName.select("em").text();
                            Log.e("cname",cname);
                            Log.e("id",id);
                            News news = new News(title,uri,cname, id);
                            newsList.add(news);//小说列表+1
                        }
                    }
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


