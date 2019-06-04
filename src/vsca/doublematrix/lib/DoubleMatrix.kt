package vsca.doublematrix.lib

import java.util.*
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
class DoubleMatrix private constructor(val rows: Int, val cols: Int, var matrix: Array<DoubleArray>) {

    companion object {

        operator fun invoke(rows: Int, cols: Int): DoubleMatrix {
            if (rows < 1 || cols < 1)
                throw RuntimeException("A matrix should have dimensions larger than 0")
            return DoubleMatrix(rows, cols, Array(rows) { DoubleArray(cols) { 0.0 } })
        }

        operator fun invoke(squareDimension: Int): DoubleMatrix {
            if (squareDimension < 1)
                throw RuntimeException("A square matrix should be at least order 1")
            return DoubleMatrix(squareDimension, squareDimension)
        }

        operator fun invoke(arrays: Array<DoubleArray>): DoubleMatrix {
            val r = arrays.size
            val c = arrays[0].size
            val matrix = this(r, c)
            matrix.matrix = arrays
            return matrix
        }

        fun rand(rows: Int, cols: Int): DoubleMatrix {
            return DoubleMatrix(
                rows,
                cols,
                Array(rows) { DoubleArray(cols) { Random.nextDouble(-1.0, 1.0) * 10 } })
        }

        fun rand(): DoubleMatrix {
            val rows = Random.nextInt(1, 11)
            val cols = Random.nextInt(1, 11)
            return DoubleMatrix(
                rows,
                cols,
                Array(rows) { DoubleArray(cols) { Random.nextDouble() * 10 } })
        }

        fun eye(n: Int): DoubleMatrix {
            val eyeMatrix = DoubleMatrix(n)
            eyeMatrix.rightDiagonal()
            return eyeMatrix
        }
    }

    operator fun get(x: Int, y: Int): Double {
        return matrix[x][y]
    }

    operator fun set(r: Int, row: DoubleArray): DoubleArray {
        for (i in 0 until cols)
            matrix[r][i] = row[i]
        return row
    }

    operator fun set(x: Int, y: Int, t: Double): Double {
        matrix[x][y] = t
        return t
    }

    operator fun unaryMinus(): DoubleMatrix {
        forEachRowColumn { i, j, value ->
            this[i, j] = -value
        }
        return this
    }

    operator fun times(b: DoubleMatrix): DoubleMatrix {
        if (cols != b.rows) {
            throw RuntimeException("Dimensions not agree")
        }

        val result = DoubleMatrix(rows, b.cols)
        result.forEachRowColumn { r, c, _ ->
            for (n in 0 until cols) {
                result[r, c] += this[r, n] * b[n, c]
            }
        }

        return result
    }

    operator fun times(b: Double): DoubleMatrix {
        val result = DoubleMatrix(rows, cols)
        this.forEachRowColumn { r, c, value ->
            result[r, c] = b * value
        }

        return result
    }

    operator fun plus(b: DoubleMatrix): DoubleMatrix {
        if (rows != b.rows || cols != b.cols) {
            throw RuntimeException("Dimensions not agree")
        }

        val sum = DoubleMatrix(rows, cols)
        sum.forEachRowColumn { r, c, _ ->
            sum[r, c] = matrix[r][c] + b[r, c]
        }

        return sum
    }

    operator fun minus(b: DoubleMatrix): DoubleMatrix {
        if (rows != b.rows || cols != b.cols) {
            throw RuntimeException("Dimensions not agree")
        }

        val sum = DoubleMatrix(rows, cols)
        sum.forEachRowColumn { r, c, _ ->
            sum[r, c] = matrix[r][c] - b[r, c]
        }

        return sum
    }

    fun transpose(): DoubleMatrix {
        val transposeMatrix = DoubleMatrix(cols, rows)
        this.forEachRowColumn { i, j, value ->
            transposeMatrix[j, i] = value
        }

        return transposeMatrix
    }

    fun clearRow(rowIndex: Int) {
        this.forEachRowColumn { i, j, _ ->
            if (i == rowIndex)
                this[i, j] = 0.0
        }
    }

    fun clearColumn(columnIndex: Int) {
        this.forEachRowColumn { i, j, _ ->
            if (j == columnIndex)
                this[i, j] = 0.0
        }
    }

    fun clearRowColumn(index: Int) {
        clearRow(index)
        clearColumn(index)
    }

    fun rightDiagonal(): DoubleMatrix {
        this.forEachRowColumn { r, c, value ->
            if (r == c && value == 0.0)
                this[r, c] = 1.0
        }

        return this
    }

    fun determinant(): Double {
        val u = LUDecomposition(this).U
        var res = 1.0
        u.forEachRowColumn { i, j, _ ->
            if (i == j)
                res *= u[i, i]
        }
        return res
    }

    fun inverse(): DoubleMatrix {
        val invMatrix = DoubleMatrix(rows, cols)
        for (i in 0 until rows) {
            val b = DoubleMatrix(rows, 1)
            b[i, 0] = 1.0
            val x = MatrixLib.solveSystem(this, b)
            invMatrix.forEachRowColumn { m, n, _ ->
                if (n == i)
                    invMatrix[m, n] = x[m, 0]
            }
        }
        return invMatrix
    }

    fun copy(): DoubleMatrix {
        val copy = DoubleMatrix(this.rows, this.cols)
        val array = Array(rows) { DoubleArray(cols) { 0.0 } }
        this.forEachRowColumn { i, j, value ->
            array[i][j] = value
        }
        copy.matrix = array
        return copy
    }

    inline fun forEachRowColumn(block: (Int, Int, Double) -> Unit) {
        var r = 0
        var c = 0
        while (r < rows) {
            while (c < cols) {
                block(r, c, matrix[r][c])
                c++
            }
            r++
            c = 0
        }
    }

    override fun toString(): String {
        val out = StringBuilder()

        for (i in 0 until rows) {
            out.append("[")
            for (j in 0 until cols) {
                val value = "%.4f".format(Locale.ENGLISH, matrix[i][j])
                out.append("$value\t")
            }
            out.append("\b]\n")
        }
        return out.toString()
    }
}
/*

fun main() {
    val a = DoubleMatrix.rand(3,3)
    println(a)
    println(a - a)
}*/
