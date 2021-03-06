package com.illusion.checkfirm

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.illusion.checkfirm.dialogs.BookmarkDialog
import com.illusion.checkfirm.bookmark.Bookmark
import com.illusion.checkfirm.search.Search
import com.illusion.checkfirm.utils.NonSwipeableViewPager
import android.graphics.Typeface
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val one = sharedPrefs.getBoolean("one", true)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        toolbar.overflowIcon = getDrawable(R.drawable.ic_more)
        setSupportActionBar(toolbar)
        val mAppBar = findViewById<AppBarLayout>(R.id.appbar)
        val height = (resources.displayMetrics.heightPixels * 0.3976)
        val lp = mAppBar.layoutParams
        lp.height = height.toInt()
        if (one) {
            mAppBar.setExpanded(true)
        } else {
            mAppBar.setExpanded(false)
        }
        val title = findViewById<TextView>(R.id.title)
        val expandedTitle = findViewById<TextView>(R.id.expanded_title)
        mAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, _ ->
            val percentage = (appBarLayout.y / appBarLayout.totalScrollRange)
            expandedTitle.alpha = 1 - (percentage * 2 * -1)
            title.alpha = percentage * -1
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.hide()
        fab.setOnClickListener {
            val bottomSheetFragment = BookmarkDialog.newInstance(false, "", "", "", -1)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        tabLayout = findViewById(R.id.mTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.firmware))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bookmark))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            if (tab != null) {
                val tabTextView = TextView(this)
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = tab.text
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD)
                    tabTextView.setTextColor(Color.parseColor("#4297ff"))
                }
            }
        }
        val mViewPager = findViewById<NonSwipeableViewPager>(R.id.mViewPager)
        val adapter = ContentsAdapter(supportFragmentManager, 2)

        mViewPager.adapter = adapter
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        tabLayout.addOnTabSelectedListener(onTabSelectedListener(mViewPager))
    }

    private fun onTabSelectedListener(pager: ViewPager): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                val text = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
                text.setTextColor(Color.parseColor("#4297ff"))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
                text.setTextColor(Color.parseColor("#7A7A7A"))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        }
    }

    inner class ContentsAdapter internal constructor(fm: FragmentManager, private var numOfTabs: Int) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return Search()
                1 -> return Bookmark()
            }
            return Search()
        }

        override fun getCount(): Int {
            return numOfTabs
        }
    }
}