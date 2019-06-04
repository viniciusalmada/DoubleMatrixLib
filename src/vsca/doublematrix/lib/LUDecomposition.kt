package vsca.doublematrix.lib

class LUDecomposition(matrix: DoubleMatrix) {
    val L: DoubleMatrix = DoubleMatrix(matrix.rows, matrix.cols)
    val U: DoubleMatrix = DoubleMatrix(matrix.rows, matrix.cols)

    init {
        L.rightDiagonal()
        matrix.forEachRowColumn { i, j, _ ->
            if (i <= j) {
                U[i, j] = matrix[i, j] - sumU(L, U, i, j)
            } else {
                L[i, j] = matrix[i, j] / U[j, j] - sumL(L, U, i, j)
            }
        }
    }

    private fun sumL(L: DoubleMatrix, U: DoubleMatrix, i: Int, j: Int): Double {
        var res = 0.0
        if (j < 1) return res
        else {
            for (n in 0 until j) {
                res += (L[i, n] * U[n, j]) / U[j, j]
            }
            return res
        }
    }

    private fun sumU(L: DoubleMatrix, U: DoubleMatrix, i: Int, j: Int): Double {
        var res = 0.0
        if (i < 1) return res
        else {
            for (n in 0 until i) {
                res += L[i, n] * U[n, j]
            }
            return res
        }
    }

    private fun sum(vararg nums: Double): Double {
        var res = 0.0
        nums.forEach { res += it }
        return res
    }
}