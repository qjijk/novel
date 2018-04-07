package com.hello.msi.novel.List;

/**
 * Created by msi on 2018/3/15.
 *
 *为小说列表加载数据
 *
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.hello.msi.novel.Data.News;
import com.hello.msi.novel.R;


public class NovelItem extends BaseAdapter {

    private List<News> newsList;
    private View view;
    private Context mContext;
    private ViewHolder viewHolder;

    public NovelItem(Context mContext, List<News> newsList) {
        this.newsList = newsList;
        this.mContext= mContext;
    }

    @Override
    public int getCount() {//返回小说列表长度
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {//返回点击位置
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {//判断是否第一次加载（好像判断有问题（目前看来每次都是重新加载））
            view = LayoutInflater.from(mContext).inflate(R.layout.news_item,
                    null);//加载new_item
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) view.findViewById(R.id.news_title);//更新id为new_title（小说名）
            //viewHolder.newsDesc = (TextView)view.findViewById(R.id.news_desc);
            viewHolder.newsTime = (TextView)view.findViewById(R.id.news_time);
            view.setTag(viewHolder);//存储标签
        } else {//如果不是第一次加载直接取出数据
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.newsTitle.setText(newsList.get(position).getNewsTitle());//获得item点击事件
        //viewHolder.newsDesc.setText("ID:"+newsList.get(position).getNewId());
        viewHolder.newsTime.setText(newsList.get(position).getNewsTime());
        return view;
    }

    class ViewHolder{
        TextView newsTitle;
        TextView newsDesc;
        TextView newsTime;
    }

}
