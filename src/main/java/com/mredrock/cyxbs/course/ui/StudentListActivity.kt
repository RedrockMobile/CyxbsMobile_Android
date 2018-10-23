package com.mredrock.cyxbs.course.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.course_fragment_time_select.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class StudentListActivity : BaseActivity() {

    companion object {
        const val COURSE_INFO = "courseInfo"
    }

//    private lateinit var mCourseInfo: Course
//    private lateinit var courseApiService: CourseApiService
//    private val retrofit: Retrofit by lazy(LazyThreadSafetyMode.NONE) {
//        Retrofit.Builder()
//                .baseUrl(CourseUrls.STUDENT_LIST_BASE_URL)
//                .client(ApiGenerator.configureOkHttp(OkHttpClient.Builder()))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//    }

    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_student_list)
//
//        mCourseInfo = intent.getParcelableExtra(COURSE_INFO)
//        courseApiService = ApiGenerator.getApiService(retrofit, CourseApiService::class.java)
//
//
//        if (mCourseInfo.courseNum != null && mCourseInfo.classroom != null) {
//            courseApiService.getStudentList(mCourseInfo.courseNum!!, mCourseInfo.classroom!!)
//                    .setSchedulers()
//                    .errorHandler()
//                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
//                        it.data?.let { students ->
//                            rv.adapter = StudentListRecAdapter(this, students)
//                        }
//                    }))
//        }
    }
}
