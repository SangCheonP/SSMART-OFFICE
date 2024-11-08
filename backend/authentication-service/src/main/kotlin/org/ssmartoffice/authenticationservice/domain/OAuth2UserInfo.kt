package org.ssmartoffice.authenticationservice.domain

class OAuth2UserInfo(
   val provider: String,
   val name: String,
   val email: String
) {

    companion object {
        fun of(provider: String?, attributes: Map<String, Any>): OAuth2UserInfo {
            return when (provider) {
                "google" -> ofGoogle(attributes)
                "kakao" -> ofKakao(attributes)
                "naver" -> ofNaver(attributes)
                else -> throw RuntimeException()
            }
        }

        private fun ofGoogle(attributes: Map<String, Any>): OAuth2UserInfo {
            return OAuth2UserInfo(
                provider = "google",
                name = attributes["name"] as String,
                email = attributes["email"] as String
            )
        }

        private fun ofKakao(attributes: Map<String, Any>): OAuth2UserInfo {
            return OAuth2UserInfo(
                provider = "kakao",
                name = (attributes["properties"] as Map<*, *>?)!!["nickname"] as String,
                email = (attributes["properties"] as Map<*, *>?)!!["email"] as String
            )
        }

        private fun ofNaver(attributes: Map<String, Any>): OAuth2UserInfo {
            return OAuth2UserInfo(
                provider = "naver",
                name = (attributes["response"] as Map<*, *>?)!!["name"] as String,
                email = (attributes["response"] as Map<*, *>?)!!["email"] as String
            )
        }
    }
}
