package dev.cappee.treble.ui

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.cappee.treble.R
import dev.cappee.treble.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var glSurfaceView: GLSurfaceView? = null
    private val bundle: Bundle = Bundle()
    private val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
            bundle.putString("GPU_INFO", gl?.glGetString(GL10.GL_VENDOR) + " " + gl?.glGetString(GL10.GL_RENDERER))
            runOnUiThread {
                glSurfaceView?.visibility = View.GONE
            }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        }

        override fun onDrawFrame(gl: GL10) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)

        //Running GPU stuff
        thread {
            glSurfaceView = GLSurfaceView(this)
            glSurfaceView?.apply {
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                setRenderer(glRenderer)
            }
            (findViewById<CoordinatorLayout>(R.id.coordinator_main)).addView(glSurfaceView)
        }


        val viewPager: ViewPager2 = findViewById(R.id.view_pager_main)
        viewPager.adapter = ViewPagerAdapter(this, bundle)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout_main)
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Treble"
                1 -> tab.text = "Root"
                2 -> tab.text = "Device"
            }
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

}