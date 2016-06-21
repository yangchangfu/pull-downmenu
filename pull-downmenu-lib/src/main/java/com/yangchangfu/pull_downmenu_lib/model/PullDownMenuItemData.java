package com.yangchangfu.pull_downmenu_lib.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/6/21.
 */
public class PullDownMenuItemData {

    public String id;
    public String name;
    public String num;
    public String icon; //示意图标

    public List<PullDownMenuItemData> itemList = new ArrayList<>();

    public PullDownMenuItemData() {

    }

    @Override
    public String toString() {
        return "PullDownMenuItemData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", icon='" + icon + '\'' +
                ", itemList=" + itemList +
                '}';
    }
}
