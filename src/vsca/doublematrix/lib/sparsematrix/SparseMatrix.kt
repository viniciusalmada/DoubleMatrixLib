package vsca.doublematrix.lib.sparsematrix

import vsca.doublematrix.lib.DoubleMatrix
import vsca.doublematrix.lib.MatrixLib
import java.util.*
import kotlin.random.Random

class SparseMatrix private constructor(val rows: Int, val cols: Int) {
    //    private val mSparseValues: HashMap<Pair<Int, Int>, Double> = HashMap()
    private val mSparseValues: SparseValues = SparseValues()

    companion object {
        operator fun invoke(rows: Int, cols: Int): SparseMatrix {
            if (rows < 1 || cols < 1)
                throw RuntimeException("A matrix should have dimensions larger than 0")
            return SparseMatrix(rows, cols)
        }

        operator fun invoke(squareDimension: Int): SparseMatrix {
            if (squareDimension < 1)
                throw RuntimeException("A square matrix should be at least order 1")
            return SparseMatrix(squareDimension, squareDimension)
        }

        fun rand(rows: Int, cols: Int = rows): SparseMatrix {
            val sm = SparseMatrix(rows, cols)
            sm.forEachRowColumn { i, j ->
                sm[i, j] = Random.nextDouble(-1.0, 1.0) * 10
            }
            return sm
        }

        fun eye(n: Int): SparseMatrix {
            val eyeSMatrix = SparseMatrix(n)
            for (i in 0 until n) {
                eyeSMatrix[i, i] = 1.0
            }
            return eyeSMatrix
        }
    }

    private fun isSquare(): Boolean = this.rows == this.cols

    fun rowsIndices(): IntArray {
        return mSparseValues.rowsIndices()
    }

    fun columnsIndices(): IntArray {
        return mSparseValues.colsIndices()
    }

    operator fun get(i: Int, j: Int): Double {
        return mSparseValues[i, j]
    }

    operator fun set(i: Int, j: Int, t: Double): Double {
        mSparseValues[i, j] = t
        return t
    }

    operator fun set(i: Int, j: Int, t: Int): Int {
        mSparseValues[i, j] = t.toDouble()
        return t
    }

    operator fun unaryMinus(): SparseMatrix {
        return this * -1
    }

    operator fun times(b: SparseMatrix): SparseMatrix {
        if (cols != b.rows) {
            throw RuntimeException("Dimensions not agree")
        }

        val result = SparseMatrix(rows, b.cols)
        result.forEachRowColumn { r, c ->
            for (n in 0 until cols) {
                result[r, c] += this[r, n] * b[n, c]
            }
        }

        return result
    }

    operator fun times(b: Double): SparseMatrix {
        val result = SparseMatrix(rows, cols)
        this.forEachValue { r, c, value ->
            result[r, c] = b * value
        }

        return result
    }

    operator fun times(b: Int): SparseMatrix {
        val result = SparseMatrix(rows, cols)
        this.forEachValue { r, c, value ->
            result[r, c] = b * value
        }

        return result
    }

    operator fun plus(b: SparseMatrix): SparseMatrix {
        if (rows != b.rows || cols != b.cols) {
            throw RuntimeException("Dimensions not agree")
        }

        val sum = SparseMatrix(rows, cols)
        sum.forEachRowColumn { r, c ->
            sum[r, c] = this[r, c] + b[r, c]
        }

        return sum
    }

    operator fun minus(b: SparseMatrix): SparseMatrix {
        return this + (b * -1)
    }

    fun transpose(): SparseMatrix {
        val transposeMatrix = SparseMatrix(cols, rows)
        this.forEachValue { i, j, value ->
            transposeMatrix[j, i] = value
        }

        return transposeMatrix
    }

    fun clearRow(rowIndex: Int) {
        this.forEachRowColumn { i, j ->
            if (i == rowIndex)
                this.mSparseValues.removeByIndex(i, j)
        }
    }

    fun clearColumn(colIndex: Int) {
        this.forEachRowColumn { i, j ->
            if (j == colIndex)
                this.mSparseValues.removeByIndex(i, j)
        }
    }

    fun clearRowColumn(index: Int) {
        clearRow(index)
        clearColumn(index)
    }

    fun rightDiagonal(): SparseMatrix {
        if (!isSquare()) throw RuntimeException("A matrix should be square")

        forEachValue { i, j, value ->
            if (i == j && value == 0.0)
                this[i, j] = 1.0
        }
        return this
    }

    fun copy(): SparseMatrix {
        val copy = SparseMatrix(this.rows, this.cols)
        this.mSparseValues.forEach {
            copy.mSparseValues[it.key.first, it.key.second] = it.value
        }
        return copy
    }

    inline fun forEachValue(block: (Int, Int, Double) -> Unit) {
        val rows = rowsIndices()
        val cols = columnsIndices()

        if (rows.size != cols.size) throw RuntimeException("A inconsistent number of values on this SparseMatrix")

        for (i in rows.indices) {
            val r = rows[i]
            val c = cols[i]
            block(r, c, this[r, c])
        }
    }

    inline fun forEachRowColumn(block: (Int, Int) -> Unit) {
        val rows = IntRange(0, this.rows - 1).step(1).toList().toIntArray()
        val cols = IntRange(0, this.cols - 1).step(1).toList().toIntArray()
        for (r in rows) {
            for (c in cols) {
                block(r, c)
            }
        }
    }

    override fun toString(): String {
        val out = StringBuilder()

        for (i in 0 until rows) {
            out.append("[")
            for (j in 0 until cols) {
                val value = "%.6f".format(Locale.ENGLISH, this[i, j])
                out.append("$value\t")
            }
            out.append("\b]\n")
        }
        return out.toString()
    }

//    data class MatrixValue(val row: Int, val col: Int, var value: Double)

    inner class SparseValues : HashMap<Pair<Int, Int>, Double>() {

        fun rowsIndices(): IntArray {
            val rows = IntArray(size)
            keys.forEachIndexed { index, pair ->
                rows[index] = pair.first
            }
            return rows
        }

        fun colsIndices(): IntArray {
            val cols = IntArray(size)
            keys.forEachIndexed { index, pair ->
                cols[index] = pair.second
            }
            return cols
        }

        fun removeByIndex(i: Int, j: Int) {
            val pair = Pair(i, j)
            remove(pair)
        }

        operator fun get(i: Int, j: Int): Double {
            val pair = Pair(i, j)
            if (containsKey(pair)) {
                val value = get(pair)!!
                return value
            }
            return 0.0
        }

        operator fun set(i: Int, j: Int, value: Double) {
            set(Pair(i, j), value)
        }
    }
}

//lateinit var timing: Timing

fun main() {
    val mK = SparseMatrix(3)
    mK[0, 0] = 270836.1054
    mK[0, 1] = 45445.9011
    mK[0, 2] = 46081.8570
    mK[1, 0] = 45445.9011
    mK[1, 1] = 521085.2896
    mK[1, 2] = -8961.2380
    mK[2, 0] = 46081.8570
    mK[2, 1] = -8961.2380
    mK[2, 2] = 152752.0314

    val mF = SparseMatrix(3, 1)
    mF[0, 0] = 50
    mF[1, 0] = -80
    mF[2, 0] = 20

    val d = MatrixLib.solveSystem(mK, mF)*100
    println(d)
}
