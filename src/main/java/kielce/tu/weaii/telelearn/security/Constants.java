package kielce.tu.weaii.telelearn.security;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {
    public static final String RESPONSE_WITH_UNAUTH_ERROR = "Responding with unauthorized error. Message - {}";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_TOKEN_BEGIN = "Bearer ";
}