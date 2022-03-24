package com.vmloft.develop.library.common.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

import java.lang.reflect.Type

/**
 * Create by lzan13 on 2021/11/12
 * 描述：Gson 转换工厂类，为了适配网络数据带有 null 的情况，这里转换成对应的空数据
 */
class GsonAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.type == String::class.java) {
            return createStringAdapter()
        }
        if (type.rawType == List::class.java || type.rawType == Collection::class.java) {
            return createCollectionAdapter(type, gson)
        }
        return null
    }

    /**
     * null替换成空List
     */
    private fun <T : Any> createCollectionAdapter(
        type: TypeToken<T>,
        gson: Gson
    ): TypeAdapter<T>? {
        val rawType = type.rawType
        if (!Collection::class.java.isAssignableFrom(rawType)) {
            return null
        }

        val elementType: Type = `$Gson$Types`.getCollectionElementType(type.type, rawType)
        val elementTypeAdapter: TypeAdapter<Any> =
            gson.getAdapter(TypeToken.get(elementType)) as TypeAdapter<Any>

        return object : TypeAdapter<Collection<Any>>() {
            override fun write(writer: JsonWriter, value: Collection<Any>?) {
                writer.beginArray()
                value?.forEach {
                    elementTypeAdapter.write(writer, it)
                }
                writer.endArray()
            }

            override fun read(reader: JsonReader): Collection<Any> {
                val list = mutableListOf<Any>()
                // null替换为空list
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull()
                    return list
                }
                reader.beginArray()
                while (reader.hasNext()) {
                    val element = elementTypeAdapter.read(reader)
                    list.add(element)
                }
                reader.endArray()
                return list
            }

        } as TypeAdapter<T>
    }

    /**
     * null 替换成空字符串
     */
    private fun <T : Any> createStringAdapter(): TypeAdapter<T> {
        return object : TypeAdapter<String>() {
            override fun write(writer: JsonWriter, value: String?) {
                if (value == null) {
                    writer.value("")
                } else {
                    writer.value(value)
                }
            }

            override fun read(reader: JsonReader): String {
                // null替换为""
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull()
                    return ""
                }
                return reader.nextString()
            }

        } as TypeAdapter<T>
    }
}
