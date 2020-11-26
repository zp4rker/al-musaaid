package com.zp4rker.almusaaid.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

/**
 * @author zp4rker
 */
data class TrackData(val track: AudioTrack, val channel: TextChannel, val requester: User)
