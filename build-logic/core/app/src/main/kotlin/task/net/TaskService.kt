package task.net

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/11/11
 * @Description:
 */
interface TaskService {
    @Multipart
    @POST("/upload_apk")
    fun uploadApk(@Part file:MultipartBody.Part): Call<ApkUploadData>

    @Headers("Content-Type: application/json")
    @POST("/cyxbsAppUpdate.json")
    fun postUpdateContent(@Body releaseData: ReleaseData):Call<ReleaseData>

    @GET("/cyxbsAppUpdate.json")
    fun getUpdateContent():Call<ReleaseData>
}