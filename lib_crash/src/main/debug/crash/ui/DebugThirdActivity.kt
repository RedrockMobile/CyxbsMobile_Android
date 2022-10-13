package crash.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DebugThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crash_activity_third)
        throw RuntimeException("模拟生命周期异常")
        findViewById<Button>(R.id.button).setOnClickListener {
            throw RuntimeException("模拟onClickCrash")
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            Thread{
                throw RuntimeException("其他线程Crash")
            }.start()
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            throw RuntimeException("主线程异常")
        }
    }
}