package com.tosuncu.springsecurityjwtplatformapp.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; //5 days
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER= "jwt-Token";
    public static final String TOKEN_CANNOT_VERIFIED= "Token cannot verified";
    public static final String TOSUNCU_LLC= "Tosuncu, LLC";
    public static final String TOSUNCU_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES ="Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to login to acces this page";
    public static final String ACCES_DENIED_MESSAGE = "You dont have permission to acces this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
   public static final String [] PUBLIC_URLS = {"/user/login", "/user/register","/user/image/**"};
 //  public static final String [] PUBLIC_URLS = {"**"};
}
