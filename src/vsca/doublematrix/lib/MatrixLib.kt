package vsca.doublematrix.lib

import vsca.doublematrix.lib.sparsematrix.LUDecompositionSparseMatrix
import vsca.doublematrix.lib.sparsematrix.SparseMatrix

class MatrixLib {
    companion object {

        fun solveSystem(a: DoubleMatrix, b: DoubleMatrix): DoubleMatrix {
            val lu = LUDecomposition(a)
            val d = DoubleMatrix(a.rows, 1)
            d.forEachRowColumn { i, _, _ ->
                d[i, 0] = b[i, 0] - sumLD(lu.L, d, i)
            }

            val x = DoubleMatrix(a.rows, 1)
            x.forEachRowColumn { row, _, _ ->
                val i = x.rows - row - 1
                x[i, 0] = d[i, 0] / lu.U[i, i] - sumUX(lu.U, x, i)
            }
            return x
        }

        fun solveSystem(a: DoubleMatrix, b: DoubleArray): DoubleMatrix {
            val lu = LUDecomposition(a)
            val d = DoubleMatrix(a.rows, 1)
            d.forEachRowColumn { i, _, _ ->
                d[i, 0] = b[i] - sumLD(lu.L, d, i)
            }

            val x = DoubleMatrix(a.rows, 1)
            x.forEachRowColumn { row, _, _ ->
                val i = x.rows - row - 1
                x[i, 0] = d[i, 0] / lu.U[i, i] - sumUX(lu.U, x, i)
            }
            return x
        }

        fun solveSystem(a: SparseMatrix, b: SparseMatrix): SparseMatrix {
            val lu = LUDecompositionSparseMatrix(a)
            val d = SparseMatrix(a.rows, 1)
            d.forEachRowColumn { i, _ ->
                d[i, 0] = b[i, 0] - sumLD(lu.L, d, i)
            }

            val x = SparseMatrix(a.rows, 1)
            x.forEachRowColumn { row, _ ->
                val i = x.rows - row - 1
                x[i, 0] = d[i, 0] / lu.U[i, i] - sumUX(lu.U, x, i)
            }
            return x
        }

        private fun sumUX(U: DoubleMatrix, X: DoubleMatrix, i: Int): Double {
            val j = X.rows
            var res = 0.0
            return if (i >= j - 1)
                res
            else {
                for (n in (i + 1) until j)
                    res += (U[i, n] * X[n, 0]) / U[i, i]
                res
            }
        }

        private fun sumLD(L: DoubleMatrix, D: DoubleMatrix, i: Int): Double {
            var res = 0.0
            return if (i < 1)
                res
            else {
                for (n in 0..i)
                    res += L[i, n] * D[n, 0]
                res
            }
        }

        private fun sumUX(U: SparseMatrix, X: SparseMatrix, i: Int): Double {
            val j = X.rows
            var res = 0.0
            return if (i >= j - 1)
                res
            else {
                for (n in (i + 1) until j)
                    res += (U[i, n] * X[n, 0]) / U[i, i]
                res
            }
        }

        private fun sumLD(L: SparseMatrix, D: SparseMatrix, i: Int): Double {
            var res = 0.0
            return if (i < 1)
                res
            else {
                for (n in 0..i)
                    res += L[i, n] * D[n, 0]
                res
            }
        }
    }
}