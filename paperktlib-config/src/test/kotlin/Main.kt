import com.filkond.paperktlib.config.templates.TitleMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
    val message = TitleMessage("test", "sub test")
    val serialized = Json.encodeToString(message)
    println(serialized)
}