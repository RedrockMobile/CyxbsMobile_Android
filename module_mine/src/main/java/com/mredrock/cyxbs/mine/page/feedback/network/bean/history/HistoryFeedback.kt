import com.google.gson.annotations.SerializedName


/**
 * @Date : 2021/9/2   20:43
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
data class HistoryFeedback(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("info")
    val info: String
)

data class Data(
    @SerializedName("feedbacks")
    val feedbacks: List<Feedback>?,
    @SerializedName("total")
    val total: Int
)

data class Feedback(
    @SerializedName("content")
    val content: String,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("DeletedAt")
    val deletedAt: Any,
    @SerializedName("ID")
    val iD: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("Redid")
    val redid: String,
    @SerializedName("replied")
    val replied: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("UpdatedAt")
    val updatedAt: String
)



