package com.hello.msi.novel.Translate;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hello.msi.novel.R;
import com.hello.msi.novel.bean.bean;
import com.hello.msi.novel.Translate.GetWordTextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;

/**
 * Created by msi on 2018/3/31.
 */

public class TextMain extends AppCompatActivity {
    private GetWordTextView mEnglishGetWordTextView;
    //private Texts tex;
    public static final String URL = "https://api.shanbay.com/bdc/search/?word=";

    protected String text_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        text_info = getIntent().getStringExtra("texts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tttxxx);
        Log.e("id",text_info);
        mEnglishGetWordTextView = (GetWordTextView) findViewById(R.id.english_get_word_text_view);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        mEnglishGetWordTextView.setText(text_info);
        mEnglishGetWordTextView.setOnWordClickListener(new GetWordTextView.OnWordClickListener() {
            @Override
            public void onClick(final String word) {
                //网络请求 TODO: 2016/12/18
                Log.e("word",word);
                OkHttpUtils
                        .get()
                        .url(URL + word.trim())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                showToast("网络错误");
                                return;

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Gson gson = new Gson();
                                Type objectType = new TypeToken<bean>() {}.getType();
                                final bean result = gson.fromJson(response, objectType);
                                if (result.getStatus_code() == 1) {

                                    Snackbar make = Snackbar.make(findViewById(android.R.id.content), result.getMsg(), Snackbar.LENGTH_LONG);
                                    SnackbarUtil.setSnackbarColor(make, Color.WHITE, Color.BLUE);
                                    make.show();

                                    return;
                                }

                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
                                View snackbarview = snackbar.getView();
                                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarview;
                                View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(R.layout.snackbar_addview, null);//加载布局文件新建View


                                TextView tv_word = (TextView) add_view.findViewById(R.id.tv_word);
                                TextView tv_phonogram = (TextView) add_view.findViewById(R.id.tv_phonogram);
                                TextView tv_info_usa = (TextView) add_view.findViewById(R.id.tv_info_usa);
                                TextView tv_info_uk = (TextView) add_view.findViewById(R.id.tv_info_uk);

                                tv_word.setText(word);
                                tv_phonogram.setText(result.getData().getCn_definition().getDefn());
                                tv_info_usa.setText("美:[" + result.getData().getPronunciations().getUs() + "]");
                                tv_info_uk.setText("英:[" + result.getData().getPronunciations().getUk() + "]");


                                add_view.findViewById(R.id.iv_usa).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        stopMedia(mediaPlayer);
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
                                        try {
                                            mediaPlayer.setDataSource(result.getData().getUs_audio());
                                            mediaPlayer.prepareAsync();//异步的准备
                                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    mp.start();
                                                }
                                            });

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                add_view.findViewById(R.id.iv_uk).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        stopMedia(mediaPlayer);

                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
                                        try {
                                            mediaPlayer.setDataSource(result.getData().getUk_audio());
                                            mediaPlayer.prepareAsync();//异步的准备
                                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    mp.start();

                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });


                                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);//设置新建布局参数

                                p.gravity = Gravity.CENTER_VERTICAL;//设置新建布局在Snackbar内垂直居中显示

                                snackbarLayout.addView(add_view, 0, p);

                                snackbar.show();

                            }
                        });
            }
        });


    }

    /**
     * 使用完MediaPlayer需要回收资源。MediaPlayer是很消耗系统资源的，所以在使用完MediaPlayer，不要等待系统自动回收，最好是主动回收资源。
     */
    private void stopMedia(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    Toast toast;

    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

}
