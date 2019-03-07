package com.mobile.hinde.utils

class Constant {
    companion object {
        const val SUN_CODE = 1
        const val MOON_CODE = 2
        const val ISS_CODE = 3
        const val INSIGHT_CODE = 4
        const val VOYAGER1_CODE = 5
        const val VOYAGER2_CODE = 6

        const val SUN_NAME = "SUN"
        const val MOON_NAME = "MOON"
        const val ISS_NAME = "ISS"
        const val INSIGHT_NAME = "INSIGHT"
        const val VOYAGER1_NAME = "VOYAGER1"
        const val VOYAGER2_NAME = "VOYAGER2"

        const val SUN_MONEY = 30
        const val MOON_MONEY = 10
        const val VOYAGER1_MONEY = 100
        const val INSIGHT_MONEY = 50

        const val VOYAGER1_UNLOCK = 500
        const val INSIGHT_UNLOCK = 200

        val TARGET_LIST = listOf(SUN_NAME, MOON_NAME, VOYAGER1_NAME, INSIGHT_NAME)
        val NAME_FROM_CODE = hashMapOf(SUN_CODE to SUN_NAME, MOON_CODE to MOON_NAME,
                ISS_CODE to ISS_NAME, INSIGHT_CODE to INSIGHT_NAME, VOYAGER1_CODE to VOYAGER1_NAME,
                VOYAGER2_CODE to VOYAGER2_NAME)

        val CODE_FROM_NAME = hashMapOf(SUN_NAME to SUN_CODE, MOON_NAME to MOON_CODE,
                ISS_NAME to ISS_CODE, INSIGHT_NAME to INSIGHT_CODE, VOYAGER1_NAME to VOYAGER1_CODE,
                VOYAGER2_NAME to VOYAGER2_CODE)

        val MONEY_FROM_NAME = hashMapOf(SUN_NAME to SUN_MONEY, MOON_NAME to MOON_MONEY,
                INSIGHT_NAME to INSIGHT_MONEY, VOYAGER1_NAME to VOYAGER1_MONEY)

        val UNLOCK_FROM_NAME = hashMapOf(VOYAGER1_NAME to VOYAGER1_UNLOCK, INSIGHT_NAME to INSIGHT_UNLOCK)

    }
}