package com.vividbobo.lemonnotes.entity


/**
 * @author: vibrantBobo
 * @date: 2021/9/21
 * @description:
 */

class NoteMedia(val type: Int, val uri: String) {
    companion object {
        val PHOTO = 0
        val VIDEO = 1
    }
}