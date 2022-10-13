package crash.ui

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.lib.crash.R

class DebugSecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crash_activity_second)
        findViewById<Button>(R.id.button).setOnClickListener {
            throw RuntimeException("模拟onClickCrash")
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            Thread {
                throw RuntimeException("其他线程Crash")
            }.start()
        }
        findViewById<Button>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, DebugThirdActivity::class.java))
        }
        findViewById<FrameLayout>(R.id.fl_exceptionView).apply {
            addView(
                object : AppCompatButton(context) {
                    var openException = false

                    init {
                        text = "view绘制异常测试"
                        setOnClickListener {
                            openException = true
                            invalidate()
                        }
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }

                    override fun onDraw(canvas: Canvas?) {
                        super.onDraw(canvas)
                        if (openException) throw RuntimeException("view OnDraw绘制异常")
                    }
                }
            )
        }
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            val data = mutableListOf<String>()
            (0..50).forEach { data.add("测试RV异常$it") }
            adapter = ExceptionRvAdapter(data)
            layoutManager = LinearLayoutManager(this.context)
        }
    }

}