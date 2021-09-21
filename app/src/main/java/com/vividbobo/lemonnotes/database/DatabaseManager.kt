package com.vividbobo.lemonnotes.database

import com.vividbobo.lemonnotes.entity.Note


class DatabaseManager(val dbHelper: DatabaseHelper) {
    val dbManager = dbHelper.writableDatabase

    fun insertNote(note: Note) {

    }
}