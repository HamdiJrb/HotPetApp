package com.example.hotpet.utils

import java.util.*

class DateUtils {
    companion object {
        fun getAge(date: Date): Int {
            val calendar = GregorianCalendar()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.time = date
            var age: Int = year - calendar.get(Calendar.YEAR)
            if (month < calendar.get(Calendar.MONTH)
                || day == calendar.get(Calendar.MONTH) && day < calendar
                    .get(Calendar.DAY_OF_MONTH)
            ) {
                --age
            }
            return age
        }

    }
}