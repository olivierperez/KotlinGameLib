package fr.o80.gamelib.pointer

import fr.o80.gamelib.PositionD
import fr.o80.gamelib.PositionF
import kotlinx.cinterop.refTo
import platform.opengl32.*
import platform.posix.fabsf

fun getPointedPosition(mousePosition: PositionD): PositionF? {
    val viewport = IntArray(4)
    val modelViewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    val winZPointer = FloatArray(1)

    glGetIntegerv(GL_VIEWPORT, viewport.refTo(0))
    glGetFloatv(GL_MODELVIEW_MATRIX, modelViewMatrix.refTo(0))
    glGetFloatv(GL_PROJECTION_MATRIX, projectionMatrix.refTo(0))

    val x = mousePosition.x.toFloat()
    val y = mousePosition.y.toFloat()

    glReadPixels(x.toInt(), y.toInt(), 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZPointer.refTo(0))

    return glhUnProjectF(
        winX = x,
        winY = viewport[3] - y,
        winZ = winZPointer[0],
        modelViewMatrix,
        projectionMatrix,
        viewport
    )?.let { (x, y) -> PositionF(x, y) }
}

/**
 * Converted code from https://www.khronos.org/opengl/wiki/GluProject_and_gluUnProject_code
 */
private fun glhUnProjectF(
    winX: Float,
    winY: Float,
    winZ: Float,
    modelView: FloatArray,
    projection: FloatArray,
    viewport: IntArray
): FloatArray? {
    // Transformation matrices
    val m = FloatArray(16)
    val a = FloatArray(16)
    val input = FloatArray(16)
    val output = FloatArray(16)

    // Calculation for inverting a matrix, compute projection x modelView
    // and store in A[16]
    multiplyMatrices4by4(a, projection, modelView)
    // Now compute the inverse of matrix A
    if (glhInvertMatrixF2(a, m) == 0)
        return null
    // Transformation of normalized coordinates between -1 and 1
    input[0] = (winX - viewport[0]) / viewport[2] * 2f - 1f
    input[1] = (winY - viewport[1]) / viewport[3] * 2f - 1f
    input[2] = 2f * winZ - 1f
    input[3] = 1f
    // Objects coordinates
    multiplyMatrixByVector4by4(output, m, input)
    if (output[3] == 0f)
        return null
    output[3] = 1f / output[3]

    return floatArrayOf(
        output[0] * output[3],
        output[1] * output[3],
        output[2] * output[3]
    )
}

private fun multiplyMatrices4by4(result: FloatArray, matrix1: FloatArray, matrix2: FloatArray) {
    result[0] = matrix1[0] * matrix2[0] +
            matrix1[4] * matrix2[1] +
            matrix1[8] * matrix2[2] +
            matrix1[12] * matrix2[3]
    result[4] = matrix1[0] * matrix2[4] +
            matrix1[4] * matrix2[5] +
            matrix1[8] * matrix2[6] +
            matrix1[12] * matrix2[7]
    result[8] = matrix1[0] * matrix2[8] +
            matrix1[4] * matrix2[9] +
            matrix1[8] * matrix2[10] +
            matrix1[12] * matrix2[11]
    result[12] = matrix1[0] * matrix2[12] +
            matrix1[4] * matrix2[13] +
            matrix1[8] * matrix2[14] +
            matrix1[12] * matrix2[15]
    result[1] = matrix1[1] * matrix2[0] +
            matrix1[5] * matrix2[1] +
            matrix1[9] * matrix2[2] +
            matrix1[13] * matrix2[3]
    result[5] = matrix1[1] * matrix2[4] +
            matrix1[5] * matrix2[5] +
            matrix1[9] * matrix2[6] +
            matrix1[13] * matrix2[7]
    result[9] = matrix1[1] * matrix2[8] +
            matrix1[5] * matrix2[9] +
            matrix1[9] * matrix2[10] +
            matrix1[13] * matrix2[11]
    result[13] = matrix1[1] * matrix2[12] +
            matrix1[5] * matrix2[13] +
            matrix1[9] * matrix2[14] +
            matrix1[13] * matrix2[15]
    result[2] = matrix1[2] * matrix2[0] +
            matrix1[6] * matrix2[1] +
            matrix1[10] * matrix2[2] +
            matrix1[14] * matrix2[3]
    result[6] = matrix1[2] * matrix2[4] +
            matrix1[6] * matrix2[5] +
            matrix1[10] * matrix2[6] +
            matrix1[14] * matrix2[7]
    result[10] = matrix1[2] * matrix2[8] +
            matrix1[6] * matrix2[9] +
            matrix1[10] * matrix2[10] +
            matrix1[14] * matrix2[11]
    result[14] = matrix1[2] * matrix2[12] +
            matrix1[6] * matrix2[13] +
            matrix1[10] * matrix2[14] +
            matrix1[14] * matrix2[15]
    result[3] = matrix1[3] * matrix2[0] +
            matrix1[7] * matrix2[1] +
            matrix1[11] * matrix2[2] +
            matrix1[15] * matrix2[3]
    result[7] = matrix1[3] * matrix2[4] +
            matrix1[7] * matrix2[5] +
            matrix1[11] * matrix2[6] +
            matrix1[15] * matrix2[7]
    result[11] = matrix1[3] * matrix2[8] +
            matrix1[7] * matrix2[9] +
            matrix1[11] * matrix2[10] +
            matrix1[15] * matrix2[11]
    result[15] = matrix1[3] * matrix2[12] +
            matrix1[7] * matrix2[13] +
            matrix1[11] * matrix2[14] +
            matrix1[15] * matrix2[15]
}

private fun multiplyMatrixByVector4by4(resultVector: FloatArray, matrix: FloatArray, pVector: FloatArray) {
    resultVector[0] =
        matrix[0] * pVector[0] + matrix[4] * pVector[1] + matrix[8] * pVector[2] + matrix[12] * pVector[3]
    resultVector[1] =
        matrix[1] * pVector[0] + matrix[5] * pVector[1] + matrix[9] * pVector[2] + matrix[13] * pVector[3]
    resultVector[2] =
        matrix[2] * pVector[0] + matrix[6] * pVector[1] + matrix[10] * pVector[2] + matrix[14] * pVector[3]
    resultVector[3] =
        matrix[3] * pVector[0] + matrix[7] * pVector[1] + matrix[11] * pVector[2] + matrix[15] * pVector[3]
}

private operator fun FloatArray.get(r: Int, c: Int): Float {
    return this[c * 4 + r]
}

private operator fun FloatArray.set(r: Int, c: Int, value: Float) {
    this[c * 4 + r] = value
}

@Suppress("DuplicatedCode")
private fun glhInvertMatrixF2(m: FloatArray, out: FloatArray): Int {
    var m0: Float
    var m1: Float
    var m2: Float
    var m3: Float
    var s: Float
    var r0 = FloatArray(8)
    var r1 = FloatArray(8)
    var r2 = FloatArray(8)
    var r3 = FloatArray(8)
    r0[0] = m[0, 0]
    r0[1] = m[0, 1]
    r0[2] = m[0, 2]
    r0[3] = m[0, 3]
    r0[4] = 1f
    r0[5] = 0f
    r0[6] = 0f
    r0[7] = 0f

    r1[0] = m[1, 0]
    r1[1] = m[1, 1]
    r1[2] = m[1, 2]
    r1[3] = m[1, 3]
    r1[4] = 0f
    r1[5] = 1f
    r1[6] = 0f
    r1[7] = 0f

    r2[0] = m[2, 0]
    r2[1] = m[2, 1]
    r2[2] = m[2, 2]
    r2[3] = m[2, 3]
    r2[4] = 0f
    r2[5] = 0f
    r2[6] = 1f
    r2[7] = 0f

    r3[0] = m[3, 0]
    r3[1] = m[3, 1]
    r3[2] = m[3, 2]
    r3[3] = m[3, 3]
    r3[4] = 0f
    r3[5] = 0f
    r3[6] = 0f
    r3[7] = 1f

    /* choose pivot - or die */
    if (fabsf(r3[0]) > fabsf(r2[0])) {
        val tmp = r3
        r3 = r2
        r2 = tmp
    }
    if (fabsf(r2[0]) > fabsf(r1[0])) {
        val tmp = r2
        r2 = r1
        r1 = tmp
    }
    if (fabsf(r1[0]) > fabsf(r0[0])) {
        val tmp = r1
        r1 = r0
        r0 = tmp
    }

    if (r0[0] == 0f)
        return 0

    /* eliminate first variable */
    m1 = r1[0] / r0[0]
    m2 = r2[0] / r0[0]
    m3 = r3[0] / r0[0]
    s = r0[1]
    r1[1] -= m1 * s
    r2[1] -= m2 * s
    r3[1] -= m3 * s
    s = r0[2]
    r1[2] -= m1 * s
    r2[2] -= m2 * s
    r3[2] -= m3 * s
    s = r0[3]
    r1[3] -= m1 * s
    r2[3] -= m2 * s
    r3[3] -= m3 * s
    s = r0[4]
    if (s != 0f) {
        r1[4] -= m1 * s
        r2[4] -= m2 * s
        r3[4] -= m3 * s
    }
    s = r0[5]
    if (s != 0f) {
        r1[5] -= m1 * s
        r2[5] -= m2 * s
        r3[5] -= m3 * s
    }
    s = r0[6]
    if (s != 0f) {
        r1[6] -= m1 * s
        r2[6] -= m2 * s
        r3[6] -= m3 * s
    }
    s = r0[7]
    if (s != 0f) {
        r1[7] -= m1 * s
        r2[7] -= m2 * s
        r3[7] -= m3 * s
    }
    /* choose pivot - or die */
    if (fabsf(r3[1]) > fabsf(r2[1])) {
        val tmp = r3
        r3 = r2
        r2 = tmp
    }
    if (fabsf(r2[1]) > fabsf(r1[1])) {
        val tmp = r2
        r2 = r1
        r1 = tmp
    }
    if (0f == r1[1])
        return 0
    /* eliminate second variable */
    m2 = r2[1] / r1[1]
    m3 = r3[1] / r1[1]
    r2[2] -= m2 * r1[2]
    r3[2] -= m3 * r1[2]
    r2[3] -= m2 * r1[3]
    r3[3] -= m3 * r1[3]
    s = r1[4]
    if (0f != s) {
        r2[4] -= m2 * s
        r3[4] -= m3 * s
    }
    s = r1[5]
    if (0f != s) {
        r2[5] -= m2 * s
        r3[5] -= m3 * s
    }
    s = r1[6]
    if (0f != s) {
        r2[6] -= m2 * s
        r3[6] -= m3 * s
    }
    s = r1[7]
    if (0f != s) {
        r2[7] -= m2 * s
        r3[7] -= m3 * s
    }
    /* choose pivot - or die */
    if (fabsf(r3[2]) > fabsf(r2[2])) {
        val tmp = r3
        r3 = r2
        r2 = tmp
    }
    if (0f == r2[2])
        return 0
    /* eliminate third variable */
    m3 = r3[2] / r2[2]
    r3[3] -= m3 * r2[3]
    r3[4] -= m3 * r2[4]
    r3[5] -= m3 * r2[5]
    r3[6] -= m3 * r2[6]
    r3[7] -= m3 * r2[7]

    /* last check */
    if (0f == r3[3])
        return 0
    s = 1f / r3[3]
    r3[4] *= s
    r3[5] *= s
    r3[6] *= s
    r3[7] *= s
    m2 = r2[3]
    s = 1f / r2[2]
    r2[4] = s * (r2[4] - r3[4] * m2)
    r2[5] = s * (r2[5] - r3[5] * m2)
    r2[6] = s * (r2[6] - r3[6] * m2)
    r2[7] = s * (r2[7] - r3[7] * m2)
    m1 = r1[3]
    r1[4] -= r3[4] * m1
    r1[5] -= r3[5] * m1
    r1[6] -= r3[6] * m1
    r1[7] -= r3[7] * m1
    m0 = r0[3]
    r0[4] -= r3[4] * m0
    r0[5] -= r3[5] * m0
    r0[6] -= r3[6] * m0
    r0[7] -= r3[7] * m0
    m1 = r1[2]
    s = 1f / r1[1]
    r1[4] = s * (r1[4] - r2[4] * m1)
    r1[5] = s * (r1[5] - r2[5] * m1)
    r1[6] = s * (r1[6] - r2[6] * m1)
    r1[7] = s * (r1[7] - r2[7] * m1)
    m0 = r0[2]
    r0[4] -= r2[4] * m0
    r0[5] -= r2[5] * m0
    r0[6] -= r2[6] * m0
    r0[7] -= r2[7] * m0
    m0 = r0[1]
    s = 1f / r0[0]
    r0[4] = s * (r0[4] - r1[4] * m0)
    r0[5] = s * (r0[5] - r1[5] * m0)
    r0[6] = s * (r0[6] - r1[6] * m0)
    r0[7] = s * (r0[7] - r1[7] * m0)
    out[0, 0] = r0[4]
    out[0, 1] = r0[5]
    out[0, 2] = r0[6]
    out[0, 3] = r0[7]
    out[1, 0] = r1[4]
    out[1, 1] = r1[5]
    out[1, 2] = r1[6]
    out[1, 3] = r1[7]
    out[2, 0] = r2[4]
    out[2, 1] = r2[5]
    out[2, 2] = r2[6]
    out[2, 3] = r2[7]
    out[3, 0] = r3[4]
    out[3, 1] = r3[5]
    out[3, 2] = r3[6]
    out[3, 3] = r3[7]
    return 1
}
