package com.modificial.hack

/**
 * Created by chenliang on 2017/12/31.
 */
object ToleranceHelper {

    fun match(r: Int, g: Int, b: Int, rt: Int, gt: Int, bt: Int, t: Int): Boolean {
        return r > rt - t &&
                r < rt + t &&
                g > gt - t &&
                g < gt + t &&
                b > bt - t &&
                b < bt + t
    }
}
