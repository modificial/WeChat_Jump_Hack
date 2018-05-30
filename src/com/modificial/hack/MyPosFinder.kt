package com.modificial.hack

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Created by chenliang on 2017/12/31.
 */
class MyPosFinder {

    fun find(image: BufferedImage?): IntArray? {
        if (image == null) {
            return null
        }
        val width = image.width
        val height = image.height

        val ret = intArrayOf(0, 0)
        var maxX = Integer.MIN_VALUE
        var minX = Integer.MAX_VALUE
        var maxY = Integer.MIN_VALUE
        var minY = Integer.MAX_VALUE
        for (i in 0 until width) {
            for (j in height / 4 until height * 3 / 4) {
                val pixel = image.getRGB(i, j)
                val r = pixel and 0xff0000 shr 16
                val g = pixel and 0xff00 shr 8
                val b = pixel and 0xff
                if (ToleranceHelper.match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 16)) {
                    maxX = Integer.max(maxX, i)
                    minX = Integer.min(minX, i)
                    maxY = Integer.max(maxY, j)
                    minY = Integer.min(minY, j)
                }
            }
        }
        ret[0] = (maxX + minX) / 2 + 3
        ret[1] = maxY
        println(maxX.toString() + ", " + minX)
        println("pos, x: " + ret[0] + ", y: " + ret[1])
        return ret
    }

    companion object {

        val R_TARGET = 40

        val G_TARGET = 43

        val B_TARGET = 86

        @Throws(IOException::class)
        @JvmStatic
        fun main(strings: Array<String>) {
            val t = MyPosFinder()
            val root = t.javaClass.getResource("/").getPath()
            println("root: $root")
            val imgsSrc = root + "imgs/src"
            val imgsDesc = root + "imgs/my_pos"
            val srcDir = File(imgsSrc)
            println(srcDir)
            var cost: Long = 0
            for (file in srcDir.listFiles()!!) {
                if (!file.getName().endsWith(".png")) {
                    continue
                }
                println(file)
                val img = ImgLoader.load(file.getAbsolutePath())
                val t1 = System.nanoTime()
                val pos = t.find(img)
                val t2 = System.nanoTime()
                cost += t2 - t1
                val desc = BufferedImage(img!!.width, img.height, BufferedImage.TYPE_INT_RGB)
                desc.graphics.drawImage(img, 0, 0, img.width, img.height, null) // 绘制缩小后的图
                desc.graphics.drawRect(pos!![0] - 5, pos[1] - 5, 10, 10)
                val descFile = File(imgsDesc, file.getName())
                if (!descFile.exists()) {
                    descFile.mkdirs()
                    descFile.createNewFile()
                }
                ImageIO.write(desc, "png", descFile)
            }
            println("avg time cost: " + cost / srcDir.listFiles()!!.size.toLong() / 1000000)

        }
    }
}
