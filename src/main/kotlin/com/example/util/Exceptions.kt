package com.example.util

/**
 * Excepciones personalizadas para la API
 */
class NotFoundException(message: String) : Exception(message)

class BadRequestException(message: String) : Exception(message)

class ConflictException(message: String) : Exception(message)

class UnauthorizedException(message: String) : Exception(message)

class ForbiddenException(message: String) : Exception(message)
