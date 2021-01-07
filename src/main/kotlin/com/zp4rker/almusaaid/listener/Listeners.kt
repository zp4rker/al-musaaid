package com.zp4rker.almusaaid.listener

/**
 * @author zp4rker
 */
object Listeners {

    fun register() {
        Reminders.register()
        DMDeleter.register()
    }

}