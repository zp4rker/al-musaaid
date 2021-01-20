package com.zp4rker.persistant.listener

/**
 * @author zp4rker
 */
object Listeners {

    fun register() {
        Reminders.register()
        DMDeleter.register()
    }

}