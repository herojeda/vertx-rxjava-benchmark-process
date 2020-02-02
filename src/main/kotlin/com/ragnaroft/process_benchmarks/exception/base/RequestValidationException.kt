package com.ragnaroft.process_benchmarks.exception.base

class RequestValidationException (
    content: String
) : BadRequestException(message = "Body: $content")
