package com.musalatask.weatherapp.common

/**
 * This class is used to tells the state in which a specific data request is.
 */
sealed class Resource<T>(var data: T? = null, val message: String? = null) {

    /**
     * The requested data was retrieved successfully.
     */
    class Success<T>(data: T): Resource<T>(data)

    /**
     * The request ends with an error.
     *
     * @param[message] is the error message.
     * @param[data] the las successfully data retrieved. This is usefully for caching scenarios,
     * when there are cached data but its updating throw an error.
     */
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)

    /**
     * The data is requesting.
     *
     * @param[data] the las successfully data retrieved. This is usefully for caching scenarios,
     * when there are cached data and it being updating.
     */
    class Loading<T>(data: T? = null): Resource<T>(data)
}