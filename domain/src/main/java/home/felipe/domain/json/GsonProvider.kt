package home.felipe.domain.json

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider {

    val shared: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapterFactory(NullableTypAdapterFactory())
            .registerTypeAdapter(LocalDateTimeSerializer::class.java, LocalDateTimeSerializer)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }
}
