package vsca.doublematrix.lib.sparsematrix


class LUDecompositionSparseMatrix(matrix: SparseMatrix) {
    val L: SparseMatrix = SparseMatrix(matrix.rows, matrix.cols)
    val U: SparseMatrix = SparseMatrix(matrix.rows, matrix.cols)

    init {
        L.rightDiagonal()
        matrix.forEachValue { i, j, value ->
            if (i <= j) {
                U[i, j] = value - sumU(L, U, i, j)
            } else {
                L[i, j] = value / U[j, j] - sumL(L, U, i, j)
            }
        }

    }

    private fun sumL(L: SparseMatrix, U: SparseMatrix, i: Int, j: Int): Double {
        var res = 0.0
        return if (j < 1) res
        else {
            for (n in 0 until j) {
                res += (L[i, n] * U[n, j]) / U[j, j]
            }
            res
        }
    }

    private fun sumU(L: SparseMatrix, U: SparseMatrix, i: Int, j: Int): Double {
        var res = 0.0
        return if (i < 1) res
        else {
            for (n in 0 until i) {
                res += L[i, n] * U[n, j]
            }
            res
        }
    }

    private fun sum(vararg nums: Double): Double {
        var res = 0.0
        nums.forEach { res += it }
        return res
    }
}