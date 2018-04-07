package com.hello.msi.novel.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.hello.msi.novel.Data.Chapters;
import com.hello.msi.novel.R;

/**
 * Created by msi on 2018/3/11.
 *为章节列表加载数据
 *估计不用变
 */

public class ChapterItem extends BaseAdapter {
    private List<Chapters> chaptersList;
    private View view;
    private Context mcContext;
    private ViewHolder viewHolder;

    public ChapterItem (Context mcContext, List<Chapters> chaptersList)
    {
        this.chaptersList = chaptersList;
        this.mcContext = mcContext;
    }

    public int getCount()//返回章节个数(ListView的长度)
    {
        return chaptersList.size();
    }

    public Object getItem(int position)
    {
        return chaptersList.get(position);//返回点击位置
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView (int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)//判断是否为第一次加载
        {
            view = LayoutInflater.from(mcContext).inflate(R.layout.chapter_item,null);//加载chapter_item
            viewHolder = new ViewHolder();
            viewHolder.chapterNum = (TextView) view.findViewById(R.id.chapter_num);//更新id为chapter_num的数据
            viewHolder.chapterName = (TextView) view.findViewById(R.id.chapter_name);//更新id为chapter_name的数据
            view.setTag(viewHolder);//存储标签
            System.out.println("第一次");
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();//取出缓存中的对象
            System.out.println("第二次");
        }


        viewHolder.chapterNum.setText(chaptersList.get(position).getChapterNum());//获得item的点击事件
        viewHolder.chapterName.setText(chaptersList.get(position).getChapterName());//获得item的点击事件

        return view;
    }

    class ViewHolder{
        TextView chapterNum;
        TextView chapterName;
    }
}

