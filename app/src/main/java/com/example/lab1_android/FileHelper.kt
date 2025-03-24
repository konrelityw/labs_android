package com.example.lab1_android

import android.content.Context
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class TextEntry(
    val text: String,
    val fontSize: Float,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

class FileHelper(private val context: Context) {

    private val fileName = "text_entries.dat"

    fun saveTextEntry(entry: TextEntry): Boolean {
        return try {
            val entries = getAllEntries().toMutableList()
            entries.add(entry)

            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(entries)
            objectOutputStream.close()
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllEntries(): List<TextEntry> {
        return try {
            if (!File(context.filesDir, fileName).exists()) {
                return emptyList()
            }

            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val entries = objectInputStream.readObject() as List<TextEntry>
            objectInputStream.close()
            fileInputStream.close()
            entries
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun clearAllEntries(): Boolean {
        return try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(emptyList<TextEntry>())
            objectOutputStream.close()
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}