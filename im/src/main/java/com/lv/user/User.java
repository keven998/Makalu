package com.lv.user;

/**
 * Created by q on 2015/5/11.
 */
public class User {
    private static User user;
    private String userId;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    private boolean isLogin;
    private User() {
    }

    public static User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
    public static void login(String userId,LoginSuccessListener listener){
        HttpManager.login(userId,listener);
    }
    public void logout(){

    }
    public String getCurrentUser(){
        return userId;
    }
    public void setCurrentUser(String userId){
        this.userId=userId;
    }
}
