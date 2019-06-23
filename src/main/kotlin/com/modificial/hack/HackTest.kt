package com.modificial.hack

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Created by modificial on 2018/1/1.
 */
object HackTest {


    @Throws(IOException::class)
    @JvmStatic
    fun main(strings: Array<String>) {
        val t = HackTest
        val root = t.javaClass.getResource("/").path
        println("root: $root")
        val imgsSrc = root + "imgs/input"
        val imgsDesc = root + "imgs/output"
        val srcDir = File(imgsSrc)
        println(srcDir)
        val myPosFinder = MyPosFinder()
        val nextCenterFinder = NextCenterFinder()
        val whitePointFinder =WhitePointFinder()

        val cost: Long = 0
        for (file in srcDir.listFiles()!!) {
            println(file)
            val img = ImgLoader.load(file.absolutePath)
            val myPos = myPosFinder.find(img)
            val nextCenter = nextCenterFinder.find(img, myPos)
            if (nextCenter == null || nextCenter[0] == 0) {
                System.err.println("find nextCenter, fail")
                continue
            } else {
                val centerX: Int
                val centerY: Int
                val whitePoint = whitePointFinder.find(img, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180)
                if (whitePoint != null) {
                    centerX = whitePoint[0]
                    centerY = whitePoint[1]
                    println("find whitePoint, succ, ($centerX, $centerY)")
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
                val desc = BufferedImage(img!!.width, img.height, BufferedImage.TYPE_INT_RGB)
                val g = desc.graphics
                g.drawImage(img, 0, 0, img.width, img.height, null)
                g.color = Color.RED
                g.fillRect(myPos!![0] - 5, myPos[1] - 5, 10, 10)
                g.color = Color.GREEN
                g.fillRect(nextCenter[0] - 5, nextCenter[1] - 5, 10, 10)
                g.fillRect(nextCenter[2] - 5, nextCenter[3] - 5, 10, 10)
                g.fillRect(nextCenter[4] - 5, nextCenter[5] - 5, 10, 10)
                g.color = Color.BLUE
                g.fillRect(centerX - 5, centerY - 5, 10, 10)
                val descFile = File(imgsDesc, file.getName())
                if (!descFile.exists()) {
                    descFile.mkdirs()
                    descFile.createNewFile()
                }
                ImageIO.write(desc, "png", descFile)
            }

        }
        println("avg time cost: " + cost / srcDir.listFiles()!!.size.toLong() / 1000000)

    }

}
