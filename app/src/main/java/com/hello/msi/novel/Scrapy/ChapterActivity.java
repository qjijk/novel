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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hello.msi.novel.Data.Texts;
import com.hello.msi.novel.List.ChapterItem;
import com.hello.msi.novel.Data.Chapters;
import com.hello.msi.novel.R;
import com.hello.msi.novel.Translate.TextMain;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URI;
import java.util.stream.Stream;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

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
                String doc = null;


                try{


                    //URI uri = new URI("https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&bookId=\"+ newsUrl");
                    //HttpGet


                    URL url = new URL("https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&bookId="+ newsUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    Log.e("sq", String.valueOf(code));
                    if(code == 200){
                        InputStream inputStream = conn.getInputStream();
                        doc = StreamTool.decodeStream(inputStream);
                        Log.e("qw", doc);

                    }
                    else {

                    }

                    JsonToXml jsonToXml = new JsonToXml.Builder(doc).build();

                    String xmlString = jsonToXml.toString();
                    //int indentationSize = 6;
                    //String formattedXml = jsonToXml.toFormattedString(indentationSize);
                    //Log.i("xmlString",xmlString);
                    System.out.println(xmlString);
                    //Log.i("formattedXml",formattedXml);





                       //解析获取标题与链接地址

                    Document docs = Jsoup.parse(xmlString);

                    Elements titleLinks = docs.select("chapterItems");    //解析获取标题与链接地址
                    //Elements ad = docs.select("li[role] > a[role] > i > svg");
                    Log.e("num",Integer.toString(titleLinks.size()));
                   // Log.e("ad",Integer.toString(ad.size()));
                    for(int j = 0;j < (titleLinks.size());j++){
                        String name = titleLinks.get(j).select("name").text();
                        String num = titleLinks.get(j).select("index").text();
                        String href = titleLinks.get(j).select("id").text();
                        //href = newsUrl + "/" + href;
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
    //https://www.webnovel.com/apiajax/chapter/GetContent?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&bookId=10026092106003405&chapterId=27480187612479847
    private void getTxts(final String urls) {
        Log.i("www","https://www.webnovel.com/apiajax/chapter/GetContent?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&bookId=" + newsUrl + "&chapterId=" + urls);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.webnovel.com/apiajax/chapter/GetContent?_csrfToken=rB7dbn17MUsR7JbKFtj4wu1i3Ys1SV0Rh0difg2Z&bookId=" + newsUrl + "&chapterId=" + urls).get();
                    Log.i("doc", String.valueOf(doc));
                    Elements tital = doc.select("body > p");
                    Log.e("title", Integer.toString(tital.size()));
                    String txt;
                    String txts = "";
                    if (tital.size() ==0)
                    {
                        Elements tita = doc.select("body");
                        Log.e("tita", String.valueOf(tita));

                    }
                    else {

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
