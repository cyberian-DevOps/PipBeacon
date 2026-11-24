package com.pipbeacon.app.core

enum class TimeFrame(val label: String, val minutes: Int) {
    M15("15M", 15),
    H1("1H", 60),
    H4("4H", 240),
    D1("1D", 1440)
}
