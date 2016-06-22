package com.yangchangfu.pull_downmenu_lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yangchangfu.pull_downmenu_lib.adapter.PullDownMenuListAdapter;
import com.yangchangfu.pull_downmenu_lib.model.PullDownMenuItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/6/20.
 */
public class PullDownMenuView extends LinearLayout {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private LinearLayout[] menuTabs;
    private ImageView[] menuIcons;    // 记录下拉菜单的Item状态图标
    private TextView[] menuTabLabels;
    private Object[] menuDatas;
    private int[] menuColumnTypes;
    private int[][] menuSelectedIndexs;

    private int selectTag = -1;

    private int menuColumn = 1;
    // tab选中图标
    private int menuSelectedIcon = R.drawable.arrow_up;
    // tab未选中图标
    private int menuUnselectedIcon = R.drawable.arrow_down;

    // tab选中颜色
    private int menuTextSelectedColor = 0xff14d0bc;
    // tab未选中颜色
    private int menuTextUnselectedColor = 0xff707070;

    private LinearLayout popup_layout;
    private PopupWindow mPopWin;
    private ListView singleList;
    private ListView rootList;
    private ListView childList;

    private OnItemSelectListener onItemSelectListener = null;

    public PullDownMenuView(Context context) {
        this(context, null);

        System.out.println("--------PullDownMenuView1---------");
    }

    public PullDownMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        System.out.println("--------PullDownMenuView2---------");
    }

    public PullDownMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        System.out.println("--------PullDownMenuView3---------");

        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        setOrientation(HORIZONTAL);//设置基布局方向为垂直方向

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullDownMenuView);
        menuColumn = a.getInteger(R.styleable.PullDownMenuView_menuTotalColumn, menuColumn);//下拉菜单的列数
        menuSelectedIcon = a.getResourceId(R.styleable.PullDownMenuView_menuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.PullDownMenuView_menuUnselectedIcon, menuUnselectedIcon);

        menuTextSelectedColor = a.getColor(R.styleable.PullDownMenuView_menuTextSelectedColor, menuTextSelectedColor);
        menuTextUnselectedColor = a.getColor(R.styleable.PullDownMenuView_menuTextUnselectedColor, menuTextUnselectedColor);

        init();
    }

    /**
     * 菜单初始化
     */
    private void init() {
        setMenuColumn(menuColumn);
    }

    /**
     * 设置菜单的列数
     *
     * @param num
     */
    private void setMenuColumn(int num) {

        //下拉菜单的列数
        if (num < 1) {
            throw new IllegalArgumentException("下拉菜单的列数必须大于1...");
        }
        menuColumn = num;

        //初始化数据
        menuTabs = new LinearLayout[menuColumn];
        menuIcons = new ImageView[menuColumn];
        menuDatas = new Object[menuColumn];
        menuTabLabels = new TextView[menuColumn];
        menuColumnTypes = new int[menuColumn];
        menuSelectedIndexs = new int[menuColumn][2];

        //循环创建菜单项
        for (int i = 0; i < menuColumn; i++) {

            menuTabs[i] = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_tab_item, null);
            menuTabLabels[i] = (TextView) menuTabs[i].findViewById(R.id.tv_item_name);
            menuTabLabels[i].setText("默认菜单" + i);
            menuTabLabels[i].setTextColor(menuTextUnselectedColor);//设置字体默认颜色
            menuIcons[i] = (ImageView) menuTabs[i].findViewById(R.id.iv_item_icon);
            menuIcons[i].setImageResource(menuUnselectedIcon);//设置图标默认状态
            menuTabs[i].setTag(i);
            menuTabs[i].setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            menuTabs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = (int) v.getTag();

                    if (selectTag == -1) {
                        selectTag = index;
                        showPopupWindow(v);
                    } else {
                        if (selectTag == index) {
                            dismissPopupWindow();
                            selectTag = -1;
                        } else {
                            dismissPopupWindow();
                            selectTag = index;
                            showPopupWindow(v);
                        }
                    }
                }
            });

            this.addView(menuTabs[i]);
        }
    }

    private void setDefaultTextColor(){
        for (int i=0; i<menuTabLabels.length; i++){
            menuTabLabels[i].setTextColor(menuTextUnselectedColor);
        }
    }

    private void setSelectedTextColor(int column){
        setDefaultTextColor();
        menuTabLabels[column].setTextColor(menuTextSelectedColor);
    }

    /**
     * 默认所有TabMenuItem右下角的图标
     */
    private void hideItemIcons(){

        for (int i=0; i<menuIcons.length; i++){
            menuIcons[i].setImageResource(menuUnselectedIcon);
        }
    }

    /**
     * 指定TabMenuItem右下角的图标为选中
     */
    private void showItemIcon(int index){
        hideItemIcons();
        menuIcons[index].setImageResource(menuSelectedIcon);
    }

    /**
     * 隐藏DropDownMenuView视图
     */
    public void dismissPopupWindow() {

        if (mPopWin != null && mPopWin.isShowing()) {
            mPopWin.dismiss();
            mPopWin = null;

            selectTag = -1;

            hideItemIcons();
            setDefaultTextColor();
        }
    }

    /**
     * 显示DropDownMenuView视图
     */
    private void showPopupWindow(View view) {

        final int index = (int) view.getTag();
        int type = menuColumnTypes[(int) view.getTag()];

        showItemIcon(index);//显示item图标选中
        setSelectedTextColor(index);

        if (type == 1) {

            // 初始化下拉列表的布局
            popup_layout = (LinearLayout) mLayoutInflater.inflate(R.layout.popup_double, null);

            // 初始化PopupWindow
            mPopWin = new PopupWindow(popup_layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            mPopWin.setFocusable(false);
            mPopWin.setOutsideTouchable(false);
            mPopWin.update();

            // mPopWin.setAnimationStyle(R.anim.dd_menu_in);
            mPopWin.setAnimationStyle(R.style.PopupWindowAnimation);

            // 设置SelectPicPopupWindow弹出窗体的背景
            ColorDrawable dw = new ColorDrawable(0x6f000000);
            mPopWin.setBackgroundDrawable(dw);

            //mPopWin.setOutsideTouchable(false);
            mPopWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            // 设置layout在PopupWindow中显示的位置
            mPopWin.showAsDropDown(view, 5, 1);

            // popView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            popup_layout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    dismissPopupWindow();//关闭弹出视图

                    return false;
                }
            });

            rootList = (ListView) popup_layout.findViewById(R.id.rootcategory);
            childList = (ListView) popup_layout.findViewById(R.id.childcategory);

            final List<PullDownMenuItemData> itemList1 = (List<PullDownMenuItemData>) menuDatas[index];
            final List<PullDownMenuItemData> itemList2;
            if (itemList1.size() > 0) {
                itemList2 = itemList1.get(menuSelectedIndexs[index][0]).itemList;
            } else {
                itemList2 = new ArrayList<PullDownMenuItemData>();
            }

            final PullDownMenuListAdapter rootAdapter = new PullDownMenuListAdapter(mContext, itemList1, true, true);
            rootList.setAdapter(rootAdapter);
            rootAdapter.selectRow(menuSelectedIndexs[index][0]);

            final PullDownMenuListAdapter childAdapter = new PullDownMenuListAdapter(mContext, itemList2, false, true);
            childList.setAdapter(childAdapter);
            childAdapter.selectRow(menuSelectedIndexs[index][1]);

            rootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    List<PullDownMenuItemData> itemList = (List<PullDownMenuItemData>) menuDatas[index];
                    List<PullDownMenuItemData> data;
                    if (itemList.size() > 0) {
                        data = itemList.get(position).itemList;
                    } else {
                        data = new ArrayList<PullDownMenuItemData>();
                    }
                    childAdapter.update(data);
                    setMenuSelectedRow(index, 0, position);//设置菜单的选中行
                    setMenuSelectedRow(index, 1, 0);//设置菜单的选中行
                    setMenuColumnLabel(index, 0, position);//设置菜单的标签
                    rootAdapter.selectRow(position);
                    childAdapter.selectRow(0);

                    if (data.size() <= 0){

                        if (onItemSelectListener != null){
                            onItemSelectListener.OnItemSelect(index, position, itemList.get(position), -1, new PullDownMenuItemData());
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                dismissPopupWindow();//关闭窗口
                            }
                        }, 300);
                    }
                }
            });

            childList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    dismissPopupWindow();//关闭窗口
                    setMenuSelectedRow(index, 1, position);//设置菜单的选中行
                    setMenuColumnLabel(index, 1, position);//设置菜单的标签
                    childAdapter.selectRow(position);

                    if (onItemSelectListener != null){
                        List<PullDownMenuItemData> itemList = (List<PullDownMenuItemData>) menuDatas[index];
                        List<PullDownMenuItemData> data = itemList.get(menuSelectedIndexs[index][0]).itemList;
                        onItemSelectListener.OnItemSelect(index, menuSelectedIndexs[index][0], itemList.get(menuSelectedIndexs[index][0]), position, data.get(position));
                    }
                }
            });

        } else {

            // 初始化下拉列表的布局
            popup_layout = (LinearLayout) mLayoutInflater.inflate(R.layout.popup_single, null);

            // 初始化PopupWindow
            mPopWin = new PopupWindow(popup_layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            mPopWin.setFocusable(false);
            mPopWin.setOutsideTouchable(false);
            mPopWin.update();

            //mPopWin.setAnimationStyle(R.anim.dd_menu_in);
            mPopWin.setAnimationStyle(R.style.PopupWindowAnimation);

            // 设置SelectPicPopupWindow弹出窗体的背景
            ColorDrawable dw = new ColorDrawable(0x6f000000);
            mPopWin.setBackgroundDrawable(dw);

            //mPopWin.setOutsideTouchable(false);
            mPopWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            // 设置layout在PopupWindow中显示的位置
            mPopWin.showAsDropDown(view, 5, 1);

            // popView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            popup_layout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    dismissPopupWindow();//关闭弹出视图

                    return false;
                }
            });

            singleList = (ListView) popup_layout.findViewById(R.id.singlelist);
            final List<PullDownMenuItemData> itemList = (List<PullDownMenuItemData>) menuDatas[index];

            final PullDownMenuListAdapter singleAdapter = new PullDownMenuListAdapter(mContext, itemList, false, true);
            singleList.setAdapter(singleAdapter);
            singleAdapter.selectRow(menuSelectedIndexs[index][0]);
            singleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    dismissPopupWindow();//关闭窗口

                    setMenuSelectedRow(index, 0, position);//设置菜单的选中行
                    setMenuColumnLabel(index, 0, position);//设置菜单的标签
                    singleAdapter.selectRow(position);

                    if (onItemSelectListener != null){
                        onItemSelectListener.OnItemSelect(index, position, itemList.get(position), -1, new PullDownMenuItemData());
                    }
                }
            });
        }
    }


    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    /**
     * 监听器的接口
     */
    public interface OnItemSelectListener {
        void OnItemSelect(int column, int position1, PullDownMenuItemData data1, int position2, PullDownMenuItemData data2);
    }

    /**
     * 列表的类型：SINGLE － 单列
     * DOUBLE － 双列
     */
    public enum Style {
        SINGLE, DOUBLE
    }

    /**
     * 设置菜单的列数据
     *
     * @param columnIndex     － 列的索引值
     * @param list            － 列的数据
     * @param style           - 列的类型：单列、双列
     * @param selectRowIndex1 - 默认选中第1列的行
     * @param selectRowIndex2 - 默认选中第2列的行
     */
    private void setMenuColumn(int columnIndex, List<PullDownMenuItemData> list, Style style, int selectRowIndex1, int selectRowIndex2) {

        if (columnIndex > menuColumn - 1 || columnIndex < 0) {
            throw new IllegalArgumentException("列的索引值不能为负数，或大于菜单的总列数...");
        }

        menuDatas[columnIndex] = list;

        if (style == Style.SINGLE) {
            menuColumnTypes[columnIndex] = 0;
        } else {
            menuColumnTypes[columnIndex] = 1;
        }

        List<PullDownMenuItemData> itemList = (List<PullDownMenuItemData>) menuDatas[columnIndex];
        if (itemList.size() > 0) {
            if (selectRowIndex1 > itemList.size() - 1) {
                selectRowIndex1 = itemList.size() - 1;
            }

            List<PullDownMenuItemData> itemList2 = itemList.get(selectRowIndex1).itemList;
            if (selectRowIndex2 != -1) {
                if (selectRowIndex2 > itemList2.size() - 1) {
                    selectRowIndex2 = itemList2.size() - 1;
                }
            }
        } else {
            selectRowIndex1 = -1;
            selectRowIndex2 = -1;
        }

        menuSelectedIndexs[columnIndex][0] = selectRowIndex1;
        menuSelectedIndexs[columnIndex][1] = selectRowIndex2;

        if (selectRowIndex2 == -1){
            setMenuColumnLabel(columnIndex, 0, selectRowIndex1);//设置菜单的标签
        } else {
            setMenuColumnLabel(columnIndex, 1, selectRowIndex2);//设置菜单的标签
        }
    }

    public void setMenuColumn(int columnIndex, List<PullDownMenuItemData> list, int selectRowIndex1, int selectRowIndex2) {
        setMenuColumn(columnIndex, list, Style.DOUBLE, selectRowIndex1, selectRowIndex2);
    }

    public void setMenuColumn(int columnIndex, List<PullDownMenuItemData> list, int selectRowIndex) {
        setMenuColumn(columnIndex, list, Style.SINGLE, selectRowIndex, -1);
    }

    /**
     * 更新列的数据
     *
     * @param columnIndex
     * @param list
     * @param selectRowIndex1
     * @param selectRowIndex2
     */
    public void updateMenuColumn(int columnIndex, List<PullDownMenuItemData> list, int selectRowIndex1, int selectRowIndex2) {
        setMenuColumn(columnIndex, list, Style.DOUBLE, selectRowIndex1, selectRowIndex2);
    }

    public void updateMenuColumn(int columnIndex, List<PullDownMenuItemData> list, int selectRowIndex) {
        setMenuColumn(columnIndex, list, Style.SINGLE, selectRowIndex, -1);
    }

    /**
     * 设置第几列的行的索引值
     *
     * @param columnIndex
     * @param subcolumnIndex 0-下拉的root列的行，1-下拉的child列的行
     * @param selectRowIndex
     */
    private void setMenuSelectedRow(int columnIndex, int subcolumnIndex, int selectRowIndex) {
        menuSelectedIndexs[columnIndex][subcolumnIndex] = selectRowIndex;
    }

    /**
     * 设置菜单的标签
     *
     * @param columnIndex
     * @param subcolumnIndex 0-下拉的root列的行，1-下拉的child列的行
     * @param selectRowIndex
     */
    private void setMenuColumnLabel(int columnIndex, int subcolumnIndex, int selectRowIndex) {

        TextView textView = menuTabLabels[columnIndex];
        List<PullDownMenuItemData> itemList = (List<PullDownMenuItemData>) menuDatas[columnIndex];

        PullDownMenuItemData data;

        if (subcolumnIndex == 0)
        {
            if (itemList.size() > 0) {
                if (selectRowIndex > itemList.size() - 1) {
                    selectRowIndex = itemList.size() - 1;
                }
                data = itemList.get(selectRowIndex);
            } else {
                data = new PullDownMenuItemData();
            }
        }
        else
        {
            if (itemList.size() > 0) {
                if (itemList.get(menuSelectedIndexs[columnIndex][0]).itemList.size() > 0) {
                    if (selectRowIndex > itemList.get(menuSelectedIndexs[columnIndex][0]).itemList.size() - 1) {
                        selectRowIndex = itemList.get(menuSelectedIndexs[columnIndex][0]).itemList.size() - 1;
                    }
                    data = itemList.get(menuSelectedIndexs[columnIndex][0]).itemList.get(selectRowIndex);
                } else {
                    data = itemList.get(menuSelectedIndexs[columnIndex][0]);
                }
            } else {
                data = new PullDownMenuItemData();
            }
        }

        textView.setText(data.name);
    }
}
