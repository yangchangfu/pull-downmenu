package com.yangchangfu.pull_downmenu_lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangchangfu.pull_downmenu_lib.R;
import com.yangchangfu.pull_downmenu_lib.model.PullDownMenuItemData;

import java.util.List;

/**
 * Created by apple on 16/6/21.
 */
public class PullDownMenuListAdapter extends BaseAdapter {

    private LayoutInflater Inflater;
    private List<PullDownMenuItemData> list;
    private boolean showRightArrow = false;
    private boolean showDigitalLabel = false;
    private int selectIndex;

    public PullDownMenuListAdapter(Context context, List<PullDownMenuItemData> list){
        this.Inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public PullDownMenuListAdapter(Context context, List<PullDownMenuItemData> list, boolean showRightArrow){
        this.Inflater = LayoutInflater.from(context);
        this.list = list;
        this.showRightArrow = showRightArrow;
    }

    public PullDownMenuListAdapter(Context context, List<PullDownMenuItemData> list, boolean showRightArrow, boolean showDigitalLabel){
        this.Inflater = LayoutInflater.from(context);
        this.list = list;
        this.showRightArrow = showRightArrow;
        this.showDigitalLabel = showDigitalLabel;
    }

    public void update(){
        notifyDataSetChanged();
    }

    public void selectRow(int position){
        this.selectIndex = position;
        notifyDataSetChanged();
    }

    public void update(List<PullDownMenuItemData> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;

        if(convertView==null)
        {
            holder=new ViewHolder();

            convertView = Inflater.inflate(R.layout.menu_list_item, null);

            holder.tv_item_name = (TextView)convertView.findViewById(R.id.tv_item_name);
            holder.tv_item_num = (TextView)convertView.findViewById(R.id.tv_item_num);
            holder.iv_enter_image = (ImageView)convertView.findViewById(R.id.iv_enter_image);
            holder.lin_list_item = (LinearLayout)convertView.findViewById(R.id.lin_list_item);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        PullDownMenuItemData data = list.get(position);
        holder.tv_item_name.setText(data.name);
        holder.tv_item_num.setText(data.num);

        if (showRightArrow){
            holder.iv_enter_image.setVisibility(View.VISIBLE);
        } else {
            holder.iv_enter_image.setVisibility(View.GONE);
        }

        if (showDigitalLabel){
            holder.tv_item_num.setVisibility(View.VISIBLE);
        } else {
            holder.tv_item_num.setVisibility(View.GONE);
        }

        if (selectIndex == position){
            holder.lin_list_item.setBackgroundResource(R.color.theme_gray_color);
        } else {
            holder.lin_list_item.setBackgroundResource(R.drawable.selector_item_bg);
        }

        return convertView;
    }

    class ViewHolder {

        private TextView     tv_item_name;
        private TextView     tv_item_num;
        private ImageView    iv_enter_image;
        private LinearLayout lin_list_item;
    }
}
