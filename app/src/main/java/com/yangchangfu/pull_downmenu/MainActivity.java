package com.yangchangfu.pull_downmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yangchangfu.pull_downmenu_lib.PullDownMenuView;
import com.yangchangfu.pull_downmenu_lib.model.PullDownMenuItemData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<PullDownMenuItemData> list0 = new ArrayList<>();
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
            }

            list0.add(data);
        }

        final List<PullDownMenuItemData> list = new ArrayList<>();

        final PullDownMenuView pullDownMenuView = (PullDownMenuView) findViewById(R.id.pulldownmenu);
        pullDownMenuView.setMenuColumn(0, list0, 30);
        pullDownMenuView.setMenuColumn(1, list0, 1, 5);
        pullDownMenuView.setMenuColumn(2, list, 5);

        pullDownMenuView.setOnItemSelectListener(new PullDownMenuView.OnItemSelectListener() {
            @Override
            public void OnItemSelect(int column, int position1, PullDownMenuItemData data1, int position2, PullDownMenuItemData data2) {
                System.out.println("----------------------------");
                System.out.println("column = " + column);
                System.out.println("position1 = " + position1);
                System.out.println("data1 = " + data1.toString());
                System.out.println("position2 = " + position2);
                System.out.println("data2 = " + data2.toString());
            }
        });

        //点击更新数据
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新数据
                pullDownMenuView.updateMenuColumn(2, list0, 5);
            }
        });
    }
}
