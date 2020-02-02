package com.ragnaroft.process_benchmarks.exception.repository.rest

import com.ragnaroft.process_benchmarks.exception.base.BadRequestException

class InternalServerClientErrorException(
    override var message: String
) : BadRequestException(message = message)
