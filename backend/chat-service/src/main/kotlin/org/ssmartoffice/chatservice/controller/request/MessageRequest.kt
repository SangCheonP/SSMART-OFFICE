package org.ssmartoffice.chatservice.controller.request

import org.ssmartoffice.chatservice.domain.MessageType


class MessageRequest(
    val type : MessageType,
    val content :String
) {

}