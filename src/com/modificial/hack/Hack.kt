package com.skyline.wxjumphack

import com.modificial.hack.ImgLoader
import com.modificial.hack.MyPosFinder
import com.modificial.hack.NextCenterFinder
import com.modificial.hack.WhitePointFinder
import java.io.File
import java.util.*

/**
 * Created by chenliang on 2018/1/1.
 */
object Hack {


    internal val ADB_PATH = "D:/Android/Sdk/platform-tools/adb"

    /**
     * 弹跳系数，现在已经会自动适应各种屏幕，请不要修改。
     */
    internal val JUMP_RATIO = 1.380

    private val RANDOM = Random()

    @JvmStatic
    fun main(strings: Array<String>) {
        val root = Hack::class.java!!.getResource("/").getPath()
        println("root: $root")
        val srcDir = File(root, "imgs/input")
        srcDir.mkdirs()
        println("srcDir: " + srcDir.getAbsolutePath())
        val myPosFinder = MyPosFinder()
        val nextCenterFinder = NextCenterFinder()
        val whitePointFinder = WhitePointFinder()
        var total = 0
        var centerHit = 0
        var jumpRatio = 0.0
        for (i in 0..4999) {
            try {
                total++
                val file = File(srcDir, i.toString() + ".png")
                if (file.exists()) {
                    file.deleteOnExit()
                }
                var process = Runtime.getRuntime().exec("$ADB_PATH shell /system/bin/screencap -p /sdcard/screenshot.png")
                process.waitFor()
                process = Runtime.getRuntime().exec(ADB_PATH + " pull /sdcard/screenshot.png " + file.getAbsolutePath())
                process.waitFor()

                println("screenshot, file: " + file.getAbsolutePath())
                val image = ImgLoader.load(file.getAbsolutePath())
                if (jumpRatio == 0.0) {
                    jumpRatio = JUMP_RATIO * 1080 / image!!.width
                }
                val myPos = myPosFinder.find(image)
                if (myPos != null) {
                    println("find myPos, succ, (" + myPos[0] + ", " + myPos[1] + ")")
                    val nextCenter = nextCenterFinder.find(image, myPos)
                    if (nextCenter == null || nextCenter[0] == 0) {
                        System.err.println("find nextCenter, fail")
                        break
                    } else {
                        val centerX: Int
                        val centerY: Int
                        val whitePoint = whitePointFinder.find(image, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180)
                        if (whitePoint != null) {
                            centerX = whitePoint[0]
                            centerY = whitePoint[1]
                            centerHit++
                            println("find whitePoint, succ, ($centerX, $centerY), centerHit: $centerHit, total: $total")
                        } else {
                            if (nextCenter[2] != Integer.MAX_VALUE && nextCenter[4] != Integer.MIN_VALUE) {
                                centerX = (nextCenter[2] + nextCenter[4]) / 2
                                centerY = (nextCenter[3] + nextCenter[5]) / 2
                            } else {
                                centerX = nextCenter[0]
                                centerY = nextCenter[1] + 48
                            }
                        }
                        println("find nextCenter, succ, ($centerX, $centerY)")
                        val distance = (Math.sqrt(((centerX - myPos[0]) * (centerX - myPos[0]) + (centerY - myPos[1]) * (centerY - myPos[1])).toDouble()) * jumpRatio).toInt()
                        println("distance: $distance")
                        val pressX = 100 + RANDOM.nextInt(500)
                        val pressY = 100 + RANDOM.nextInt(500)
                        val adbCommand = ADB_PATH + String.format(" shell input swipe %d %d %d %d %d", pressX, pressY, pressX, pressY, distance)
                        println(adbCommand)
                        Runtime.getRuntime().exec(adbCommand)
                    }
                } else {
                    System.err.println("find myPos, fail")
                    break
                }
            } catch (e: Exception) {
                e.printStackTrace()
                break
            }

            try {
                // sleep 随机时间，防止上传不了成绩
                Thread.sleep((3000 + RANDOM.nextInt(4000)).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
        println("centerHit: $centerHit, total: $total")
    }

}
