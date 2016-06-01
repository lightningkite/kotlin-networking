package com.ivieleague.kotlin.networking

/**
 * Created by shanethompson on 1/27/16.
 */
class MockNetStack(val createMockResponse: (url: String, body: NetBody?, headers: Map<String, String>) -> NetStream) : NetStack {
    override fun stream(request: NetRequest): NetStream {
        return createMockResponse(request.url, request.body, request.headers)
    }

}