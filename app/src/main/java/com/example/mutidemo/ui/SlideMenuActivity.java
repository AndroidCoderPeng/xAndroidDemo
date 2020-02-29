package com.example.mutidemo.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.jkb.slidemenu.SlideMenuLayout;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/28 15:52
 */
public class SlideMenuActivity extends BaseNormalActivity {

    private static final String TAG = "SlideMenuActivity";

    @BindView(R.id.menuListView)
    ListView menuListView;
    @BindView(R.id.contentTabLayout)
    TabLayout contentTabLayout;
    @BindView(R.id.contentView)
    ViewPager contentView;
    @BindView(R.id.contentLayout)
    LinearLayout contentLayout;
    @BindView(R.id.mainSlideMenu)
    SlideMenuLayout mainSlideMenu;

    private static List<Integer> menuImageList = Arrays.asList(R.drawable.dashboard,
            R.drawable.dashboard, R.drawable.dashboard, R.drawable.dashboard);
    private static List<String> menuItemList = Arrays.asList("头条", "壁纸", "音乐", "电影");
    private GestureDetector mGestureDetector;

    @Override
    public void initView() {
        setContentView(R.layout.activity_slidemenu);
    }

    @Override
    public void initData() {
        SlideMenuAdapter menuAdapter = new SlideMenuAdapter(this);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EasyToast.showToast(menuItemList.get(position), EasyToast.SUCCESS);
            }
        });
    }

    @Override
    public void initEvent() {
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) < 150) {
                    Log.d(TAG, "onFling: 滑动太慢");
                    return true;
                }
                if ((e1.getRawX() - e2.getRawX()) > 200) {
                    Log.d(TAG, "onFling: 左滑");
                    mainSlideMenu.closeLeftSlide();
                    return true;
                }
                if ((e2.getRawX() - e1.getRawX()) > 200) {
                    Log.d(TAG, "onFling: 右滑");
                    mainSlideMenu.openLeftSlide();
                    return true;//消费掉当前事件  不让当前事件继续向下传递
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mainSlideMenu.isLeftSlideOpen()) {
            mainSlideMenu.closeLeftSlide();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * onTouch监听
     */
    //重写activity的触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让手势识别器生效
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //侧滑菜单列表
    static class SlideMenuAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        SlideMenuAdapter(Context mContext) {
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return menuImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return menuImageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewHolder itemHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_menu_listview, null);
                itemHolder = new ItemViewHolder();
                itemHolder.itemView = convertView.findViewById(R.id.itemView);
                itemHolder.itemTitle = convertView.findViewById(R.id.itemTitle);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemViewHolder) convertView.getTag();
            }
            itemHolder.bindHolder(menuImageList.get(position), menuItemList.get(position));
            return convertView;
        }

        static class ItemViewHolder {

            private ImageView itemView;
            private TextView itemTitle;

            void bindHolder(int resId, String item) {
                itemView.setBackgroundResource(resId);
                itemTitle.setText(item);
            }
        }
    }
}
