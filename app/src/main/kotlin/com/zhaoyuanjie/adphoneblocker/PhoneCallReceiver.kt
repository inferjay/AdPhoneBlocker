package com.zhaoyuanjie.adphoneblocker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.telephony.TelephonyManager
import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

/**
 * 监听打入电话的receiver
 * Created by zhaoyuanjie on 15/12/26.
 */
class PhoneCallReceiver: BroadcastReceiver() {

    private var call: Call<QueryResult>? = null;
    private val notifyId = 54256;

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            call = Restful.baiduApi().query(number, true)
            call?.enqueue(object: Callback<QueryResult> {
                override fun onResponse(response: Response<QueryResult>, retrofit: Retrofit) {
                    val info = response.body().response[number]
                    if (info?.name?.isNullOrEmpty()?.not() ?: false) {
                        val builder = NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_stat_action_settings_phone)
                                .setContentTitle(info?.name)
                                .setContentText(context.getString(R.string.marked_count, info?.count))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(notifyId, builder.build())
                    }
                }

                override fun onFailure(t: Throwable) {

                }
            })
        } else {
            call?.cancel()
            call = null
        }
    }
}