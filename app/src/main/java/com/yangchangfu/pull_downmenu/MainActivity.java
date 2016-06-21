package com.yangchangfu.pull_downmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yangchangfu.pull_downmenu_lib.PullDownMenuView;
import com.yangchangfu.pull_downmenu_lib.model.PullDownMenuItemData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<PullDownMenuItemData> list0 = new ArrayList<>();
        for (int i=0; i<15; i++){
            PullDownMenuItemData data = new PullDownMenuItemData();
            data.id = "" + i;
            data.name = "主分类" + i;
            data.num = String.valueOf(10 * i);

            List<PullDownMenuItemData> itemList = new ArrayList<>();
            for (int j=0; j<6; j++){
                PullDownMenuItemData sub = new PullDownMenuItemData();
                sub.id = "" + i;
                sub.name = "子分类:" + i + "-" + j;
                sub.num = String.valueOf(5 * i);
                itemList.add(sub);
            }

            if (i % 2 == 0) {
                data.itemList = itemList;
            } else {
                //data.itemList = new ArrayList<>();
            }

            list0.add(data);
        }

        PullDownMenuView pullDownMenuView = (PullDownMenuView) findViewById(R.id.pulldownmenu);
        pullDownMenuView.setMenuColumn(0, list0, 0);
        pullDownMenuView.setMenuColumn(1, list0, 0, 0);
        pullDownMenuView.setMenuColumn(2, list0, 0);

        pullDownMenuView.setOnItemSelectListener(new PullDownMenuView.OnItemSelectListener() {
            @Override
            public void OnItemSelect(int column, int position, PullDownMenuItemData data1, int position2, PullDownMenuItemData data2) {
                System.out.println("----------------------------");
                System.out.println("column = " + column);
                System.out.println("position = " + position);
                System.out.println("data1 = " + data1.toString());
                System.out.println("position2 = " + position2);
                System.out.println("data2 = " + data2.toString());
            }
        });
    }
}
