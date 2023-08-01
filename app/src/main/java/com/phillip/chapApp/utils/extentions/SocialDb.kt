package com.phillip.chapApp.utils.extentions

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.GsonBuilder
import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.model.Message
import com.phillip.chapApp.model.Sticker
import com.phillip.chapApp.model.User
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset


object SocialDb {

    fun loadChannel(context: Context): ArrayList<Channel> {
        val feedList: ArrayList<Channel> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "chat.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: Channel =
                    gson.fromJson(listChanel.getString(i), Channel::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadChatCaNhan(context: Context): ArrayList<Channel> {
        val feedList: ArrayList<Channel> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "chat_canhan.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: Channel =
                    gson.fromJson(listChanel.getString(i), Channel::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadMessage(context: Context): ArrayList<Message> {
        val feedList: ArrayList<Message> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "message.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: Message =
                    gson.fromJson(listChanel.getString(i), Message::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadSaveMessage(context: Context): ArrayList<Message> {
        val feedList: ArrayList<Message> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "save_message.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: Message =
                    gson.fromJson(listChanel.getString(i), Message::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadStickers(context: Context): ArrayList<Sticker> {
        val feedList: ArrayList<Sticker> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "sticker.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: Sticker =
                    gson.fromJson(listChanel.getString(i), Sticker::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadUser(context: Context): ArrayList<User> {
        val feedList: ArrayList<User> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "contacts.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: User =
                    gson.fromJson(listChanel.getString(i), User::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadMemberChannel(context: Context): ArrayList<User> {
        val feedList: ArrayList<User> = ArrayList()
        return try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val data = JSONObject(loadJSONFromAsset(context, "members.json"))
            val listChanel = data.getJSONArray("data")
            for (i in 0 until listChanel.length()) {
                val feed: User =
                    gson.fromJson(listChanel.getString(i), User::class.java)
                feedList.add(feed)
            }
            return feedList

        } catch (e: java.lang.Exception) {
            return feedList
        }
    }

    fun loadJSONFromAsset(context: Context, path: String): String {
        var json: String = ""
        var intputStream: InputStream
        try {
            val manager: AssetManager = context.getAssets()

            intputStream = manager.open(path)
            val size: Int = intputStream.available()
            val buffer = ByteArray(size)
            intputStream.read(buffer)
            intputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (e: Exception) {
            e.printStackTrace()
            return json
        }
        return json
    }

}