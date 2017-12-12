package com.wvvw.toolbar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruanwj on 2017/9/20.
 * Desc ${TODO}.
 */

public class QQz extends AppCompatActivity {
    private Toolbar      mToolbar;
    private ListView     mListView;
    private List<String> mDatas;
    private LinearLayout mLayout;

    //listView的头
    private View headView;

    //listView头中包含的布局。这里仅仅是一个ImageView
    private ImageView head_iv;

    //listview的头部（这里是ImageView）顶部距离屏幕顶部（包含状态栏）的距离
    //注：这个高度，是头布局在屏幕里才会计算的，出了屏幕，就不会变了
    private int height;

    //listView的头部的真实高度。头布局的整体高度，因为这个demo只简单写了个ImageView作为头部，所以ImageView的高度，就是头部的高度
    private int headViewHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qq);


        //这里配置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initData();


        headView = LayoutInflater.from(this).inflate(R.layout.head, null);
        head_iv = (ImageView) headView.findViewById(R.id.head_iv);

        //初始化透明度
        mLayout = (LinearLayout) findViewById(R.id.toolbar_content);
        mLayout.getBackground().setAlpha(0);//0-255

        mToolbar = (Toolbar) findViewById(R.id.qq_toolbar);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.addHeaderView(headView);
        mListView.setAdapter(new TestAdapter());

        // Logo
        //toolbar.setLogo(R.mipmap.ic_launcher);

        // 主标题(此处设置为空主要是因为我在布局中自定义了一个标题并把它居中显示)
        mToolbar.setTitle("");

        // 副标题
        //toolbar.setSubtitle("Sub Title");

        //设置toolbar
        setSupportActionBar(mToolbar);

        //左边的小箭头（注意需要在setSupportActionBar(toolbar)之后才有效果）
        mToolbar.setNavigationIcon(R.mipmap.store_home_tab_index_pre);

        //菜单点击事件（注意需要在setSupportActionBar(toolbar)之后才有效果）
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);


        //设置滚动侦听
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int location[] = new int[2];

                Drawable drawable = mLayout.getBackground();
                if (drawable == null || firstVisibleItem != 0){
                    return;
                }


                //head_iv相对于当期窗口（window）左上角的位置
                head_iv.getLocationInWindow(location);

                //listview的头部（这里是ImageView）顶部距离屏幕顶部（包含状态栏）的距离
                height = Math.abs(location[1]);
                headViewHeight = head_iv.getHeight();

                if (height<0){
                    height = 0;
                }

                if (height>255){
                    height=255;
                }

                drawable.setAlpha(height);
                Log.d("toolbar","height="+height);

            }

        });

    }


    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mDatas.add("I ma " + i + " Items!");
        }
    }


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    msg += "Click edit";
                    break;
                case R.id.action_share:
                    msg += "Click share";
                    break;
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }

            if (!msg.equals("")) {
                Toast.makeText(QQz.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 绑定toobar跟menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TestHolder holder;
            if (convertView == null) {
                holder = new TestHolder();
                convertView = View.inflate(QQz.this, R.layout.item, null);
                holder.mTextView = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(holder);
            } else {
                holder = (TestHolder) convertView.getTag();
            }

            holder.mTextView.setText(mDatas.get(position));

            return convertView;
        }


        class TestHolder {
            TextView mTextView;
        }
    }
}
