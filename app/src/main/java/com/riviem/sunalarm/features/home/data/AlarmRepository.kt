package com.riviem.sunalarm.features.home.data

import com.riviem.sunalarm.features.home.data.models.Alarm

interface AlarmRepository {

    fun getAlarms(): List<Alarm>
}