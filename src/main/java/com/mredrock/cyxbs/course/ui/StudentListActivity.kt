package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.StudentListRecAdapter
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.network.CourseUrls
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import kotlinx.android.synthetic.main.course_activity_student_list.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class StudentListActivity : BaseActivity() {

    companion object {
        const val DEFAULT_TIME_OUT = 30L
        const val COURSE_INFO = "courseInfo"
    }

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var mCourseInfo: Course
    private lateinit var courseApiService: CourseApiService
    private val retrofit: Retrofit by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
                .baseUrl(CourseUrls.STUDENT_LIST_BASE_URL)
                .client(configureOkHttp(OkHttpClient.Builder()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_student_list)

//        (tb as JToolbar).titleTextView.text = resources.getString(R.string.course_student_list)

        mCourseInfo = intent.getParcelableExtra(COURSE_INFO)
        courseApiService = ApiGenerator.getApiService(retrofit, CourseApiService::class.java)


        if (mCourseInfo.teacher != null && mCourseInfo.classroom != null) {
//            (tb as JToolbar).titleTextView.text = mCourseInfo.courseNum

            courseApiService.getStudentList(mCourseInfo.teacher!!, BaseApp.user!!.stuNum!!, mCourseInfo.classroom!!)
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                        it.data?.let { students ->
                            course_auto.adapter = StudentListRecAdapter(this, students)
                        }
                    }))
        }
    }

    private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        return builder.build()
    }
}
