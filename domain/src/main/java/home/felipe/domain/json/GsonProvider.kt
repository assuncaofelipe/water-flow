package home.felipe.domain.json

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

object GsonProvider {

    val shared: Gson by lazy {
        val localDateTimeType = object : TypeToken<LocalDateTime>() {}.type

        GsonBuilder()
            .registerTypeAdapterFactory(NullableTypAdapterFactory())
            .registerTypeAdapter(localDateTimeType::class.java, LocalDateTimeSerializer)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }
}
