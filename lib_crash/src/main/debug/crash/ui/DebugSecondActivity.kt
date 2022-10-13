package crash.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DebugSecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crash_activity_second)
        findViewById<Button>(R.id.button).setOnClickListener {
            throw RuntimeException("模拟onClickCrash")
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            Thread{
                throw RuntimeException("其他线程Crash")
            }.start()
        }
        findViewById<Button>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, DebugThirdActivity::class.java))
        }
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            val data = mutableListOf<String>()
            (0..50).forEach { data.add("测试RV异常$it") }
            adapter = ExceptionRvAdapter(data)
            layoutManager = LinearLayoutManager(this.context)
        }
    }
    
}