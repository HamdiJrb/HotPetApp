package com.example.hotpet.utils

import android.app.AlertDialog
import android.content.Context

class AlertMaker {
    companion object {
        fun makeAlert(context: Context, title: String, message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)

            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        fun makeActionAlert(context: Context, title: String, message: String, function: () -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)

            builder.setPositiveButton("Ok") { dialog, _ ->
                function()
                dialog.dismiss()
            }

            builder.show()
        }
    }
}