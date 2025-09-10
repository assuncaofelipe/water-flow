package home.felipe.domain.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object LocalDateTimeSerializer : JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {
    private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src == null) JsonNull.INSTANCE
        else JsonPrimitive(src.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        return if (json?.isJsonPrimitive == true && json.asJsonPrimitive.isString) {
            try {
                LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
            } catch (ex: DateTimeParseException) {
                null
            }
        } else null
    }
}