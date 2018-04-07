package com.hello.msi.novel.Scrapy;

/**
 * Created by msi on 2018/3/15.
 * 章节爬虫
 */

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hello.msi.novel.Data.Texts;
import com.hello.msi.novel.List.ChapterItem;
import com.hello.msi.novel.Data.Chapters;
import com.hello.msi.novel.R;
import com.hello.msi.novel.Translate.TextMain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    private String newsUrl;
    private List<Chapters> chapterList;
    private ChapterItem adapter;
    private Handler handler;
    private ListView lv;
    private Texts texts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        newsUrl = getIntent().getStringExtra("news_url");
        Log.i("web","https://m.webnovel.com"+ newsUrl + "/catalog");
        chapterList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.chpter_lv);//获取布局文件中的ListView控件对象命名为lv
        getChpter();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    adapter = new ChapterItem(ChapterActivity.this,chapterList); //创建数组适配器对象
                    lv.setAdapter(adapter);//把数组适配器加载到ListView控件中
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置事件监听
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Chapters news = chapterList.get(position);//获取点击位置
                            getTxts(news.getChapterHref());
                        }
                    });
                }
            }
        };

    }

    /**
     * 获取章节信息
     */

    private void getChpter(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect("https://m.webnovel.com"+ newsUrl + "/catalog").get();
                    Elements titleLinks = doc.select("ol.contents > li");    //解析获取标题与链接地址
                    Elements ad = doc.select("li[role] > a[role] > i > svg");
                    Log.e("num",Integer.toString(titleLinks.size()));
                    Log.e("ad",Integer.toString(ad.size()));
                    for(int j = 0;j < (titleLinks.size()-ad.size());j++){
                        String name = titleLinks.get(j).select("a > span").text();
                        String num = titleLinks.get(j).select("a > i").text();
                        String href = titleLinks.get(j).select("a").attr("href");
                        Log.i(num,name);
                        Log.i("www",href);
                        Chapters chapters = new Chapters(num, name, href);
                        chapterList.add(chapters);//章节列表+1
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     *获取文字信息
     */
    private void getTxts(final String urls) {
        Log.i("www",urls);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://m.webnovel.com" + urls ).get();
                    Elements tital = doc.select("div.j_showSetModal.j_chaTxt.cha-txt > p");
                    Log.e("title", Integer.toString(tital.size()));
                    String txt;
                    String txts = "";
                    for (int j = 0; j < (tital.size()); j++) {

                        txt = tital.get(j).select("p").text();
                        if(txt.equals(""))
                        {

                        }

                        else
                        {
                            txts = txts + txt + '\n' + '\n';

                            Log.i("txt", txt);
                            Log.i("txtss", txts);
                            Texts texts = new Texts(txt);
                        }

                    }
                    Intent intent = new Intent(ChapterActivity.this,TextMain.class);//传递小说内容到TextMain
                    intent.putExtra("texts",txts);
                    startActivity(intent);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
