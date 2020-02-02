package com.ragnaroft.process_benchmarks.exception.base

class ExceptionResolver {

    companion object{
        fun resolve4xx(statusCode: Int, message: String): ErrorRequestException {
            return when (statusCode) {
                400 -> BadRequestException(message)
                401 -> UnauthorizedException(message)
                403 -> ForbiddenException(message)
                404 -> NotFoundException(message)
                else -> BadRequestException(message)
            }
        }
    }
}
