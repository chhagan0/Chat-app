package com.example.vivd.ModelClass

class Message {
    var messageId: String? = null
    var message: String? = null
    var senderId: String? = null
    var imageUrl: String? = null
    var timestamp: Long = 0

    constructor() {

    }

    constructor(message: String?, messageId: String?, timestamp: Long) {

        this.message = message
        this.messageId = messageId
        this.timestamp = timestamp
    }


}