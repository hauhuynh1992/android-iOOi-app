package com.phillip.chapApp.api

import com.phillip.chapApp.model.*
import com.phillip.chapApp.model.response.CreateChannelResponse
import com.phillip.chapApp.model.response.LisGroupResponse
import com.phillip.chapApp.model.response.SearchUserResponse
import com.phillip.chapApp.model.response.SendMessageResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


interface ItemAPI {

    /* Auth */
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("CodeNo") username: String,
        @Field("Password") password: String
    ): Observable<LoginData>

    @POST("log/logout")
    fun logout(
    ): Observable<BaseItemResponse<String>>

    @GET("notification")
    fun getNotification(
    ): Observable<BaseItemResponse<Notification>>

    @FormUrlEncoded
    @POST("user/update-password")
    fun updatePassword(
        @Field("password_old") oldPassword: String,
        @Field("password_new") newPassword: String,
        @Field("password_confirm") confirmPassword: String
    ): Observable<BaseItemResponse<String>>

    @GET("app/message")
    fun getAppInfo(
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("message/clear-all-chat")
    fun clearAllChat(@Field("SocketId") socketId: String? = null): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("message/clear-group-chat")
    fun clearGroupChatHistory(@Field("group_id") groupId: Int): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST(" message/clear-one-chat")
    fun clearOneOneChatHistory(@Field("receiver_id") receiverId: Int): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("user/updateSocket")
    fun updateSocket(
        @Field("SocketId") socketId: String
    ): Observable<BaseItemResponse<String>>

    @Multipart
    @POST("upload/clients/photo")
    fun uploadPhoto(@Part avatar: MultipartBody.Part): Observable<BaseItemResponse<FileData>>

    @Multipart
    @POST("upload/clients/file")
    fun uploadFile(@Part file: MultipartBody.Part): Observable<BaseItemResponse<FileData>>

    @FormUrlEncoded
    @POST("user/update-info")
    fun updateAccount(
        @Field("image") image: String? = null,
        @Field("email") email: String? = null,
        @Field("password") password: String? = null,
        @Field("name") name: String? = null,
        @Field("address") address: String? = null,
        @Field("phone") phone: String? = null,
        @Field("bio") bio: String? = null,
        @Field("time_load_message") time_load_message: Long? = null
    ): Observable<BaseItemResponse<String>>

    /* Friend */
    @POST("friend/list/receive")
    fun getFriendsRequestReceive(): Observable<BaseItemResponse<ArrayList<User>>>

    @GET("friend/list/pending")
    fun getFriendsRequestSent(): Observable<BaseItemResponse<ArrayList<User>>>

    @GET("time-block")
    fun getTimeLock(): Observable<BaseItemResponse<TimeLock>>

    @FormUrlEncoded
    @POST("friend/add")
    fun sendFriendRequest(@Field("friend_id") friendId: Int): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("friend/accept")
    fun acceptFriendRequest(@Field("sender_id") senderId: Int): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("friend/update-name")
    fun updateFriendName(
        @Field("friend_id") friendId: Int,
        @Field("name") name: String
    ): Observable<BaseItemResponse<String>>

    @GET("friend/list-name")
    fun getListFriendName(): Observable<BaseItemResponse<ArrayList<FriendName>>>

    @FormUrlEncoded
    @POST("friend/cancel-request")
    fun cancelFriendRequest(@Field("friend_id") friendId: Int): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("friend/remove")
    fun removeFriend(@Field("friend_id") friendId: Int): Observable<BaseItemResponse<String>>

    @POST("friend/list")
    fun getFriends(): Observable<BaseItemResponse<ArrayList<User>>>

    @FormUrlEncoded
    @POST("user/search")
    fun searchFriends(@Field("key") key: String): Observable<SearchUserResponse>

    @FormUrlEncoded
    @POST("friend/status")
    fun checkFriend(@Field("friend_id") friendId: Int): Observable<BaseItemResponse<String>>

    /* Group */
    @FormUrlEncoded
    @POST("group/create")
    fun createGroup(
        @Field("name") name: String,
        @Field("image") image: String? = null,
        @Field("user_id") userIds: List<Int>
    ): Observable<CreateChannelResponse>

    @FormUrlEncoded
    @POST("message/list-message")
    fun getChannelOneToOne(@Field("name") temp: Any)
            : Observable<BaseListResponse<Message>>

    @FormUrlEncoded
    @POST("group/message/list-message")
    fun getChannelGroup(@Field("name") temp: Any)
            : Observable<LisGroupResponse>

    @FormUrlEncoded
    @POST("message/list")
    fun getListMessagesOneToOne(
        @Field("receiver_id") receiverId: Int,
        @Field("limit") limit: Int,
        @Field("page") page: Int
    ): Observable<BaseListResponse<Message>>

    @FormUrlEncoded
    @POST("group/message/list")
    fun getListMessagesGroup(
        @Field("group_id") groupId: Int,
        @Field("limit") limit: Int,
        @Field("page") page: Int
    ): Observable<BaseListResponse<Message>>

    @FormUrlEncoded
    @POST("group/user/add")
    fun addGroupMember(
        @Field("group_id") groupId: Int,
        @Field("user_id") userIds: List<Int>
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("group/remove")
    fun deleteGroup(
        @Field("group_id") groupId: Int
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("group/message/update-read")
    fun readAllMessagesGroup(
        @Field("group_id") groupId: Int
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("message/update-user-read")
    fun readAllMessagesOneOne(
        @Field("receiver_id") receiverId: Int
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("group/message/read")
    fun getViewedMemberOfGroup(
        @Field("group_id") groupId: Int
    ): Observable<BaseListResponse<Member>>

    @FormUrlEncoded
    @POST("message/user-read")
    fun checkUserReadMessageOneOne(
        @Field("receiver_id") receiverId: Int
    ): Observable<BaseListResponse<Member>>

    @FormUrlEncoded
    @POST("group/user/leave")
    fun leaveGroup(
        @Field("group_id") groupId: Int,
        @Field("user_id") userId: Int
    ): Observable<BaseItemResponse<String>>


    @FormUrlEncoded
    @POST("group/list-user")
    fun getGroupMembers(@Field("group_id") groupId: Int): Observable<BaseListResponse<User>>

    @FormUrlEncoded
    @POST("group/user/remove")
    fun removeGroupMember(
        @Field("group_id") groupId: Int,
        @Field("user_id") memberId: Int
    ): Observable<BaseItemResponse<String>>

    @FormUrlEncoded
    @POST("group/update")
    fun updateGroupInfo(
        @Field("group_id") group_id: Int,
        @Field("name") name: String,
        @Field("image") image: String? = null
    ): Observable<BaseItemResponse<String>>

    /* Message */
    @FormUrlEncoded
    @POST("message/add")
    fun sendMessageOneToOne(
        @Field("receiver_id") receiverId: Int,
        @Field("type") type: Int,
        @Field("content") content: String,
        @Field("info_file") info_file: String? = "",
        @Field("parent_id") parent_message_id: Int? = null,
        @Field("parent_sender_id") parent_sender_id: Int? = null,
        @Field("time") time: Long? = null
    ): Observable<SendMessageResponse>

    @FormUrlEncoded
    @POST("group/message/delete")
    fun deleteMessageGroup(
        @Field("group_id") groupId: Int,
        @Field("message_id") messageId: Int
    ): Observable<SendMessageResponse>

    @FormUrlEncoded
    @POST("message/delete")
    fun deleteMessageOne(
        @Field("receiver_id") receiverId: Int,
        @Field("message_id") messageId: Int
    ): Observable<SendMessageResponse>

    @FormUrlEncoded
    @POST("group/message/add")
    fun sendMessageGroup(
        @Field("group_id") groupId: Int,
        @Field("type") type: Int,
        @Field("content") content: String,
        @Field("info_file") info_file: String? = "",
        @Field("parent_id") parent_message_id: Int? = null,
        @Field("parent_sender_id") parent_sender_id: Int? = null,
        @Field("time") time: Long? = null
    ): Observable<SendMessageResponse>

    @FormUrlEncoded
    @POST("question/create")
    fun sendQuestionOneToOne(
        @Field("receiver_id") receiverId: Int,
        @Field("question") question: String,
        @Field("answer") answer: List<String>,
        @Field("correct") correct: List<Int>
    ): Observable<SendMessageResponse>

    @FormUrlEncoded
    @POST("question/reply")
    fun answerQuestionOneToOne(
        @Field("receiver_id") receiverId: Int,
        @Field("question_id") questionId: Int,
        @Field("answer_text") answer: String
    ): Observable<BaseItemResponse<String>>
}