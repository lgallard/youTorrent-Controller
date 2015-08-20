package com.lgallardo.youtorrentcontroller;

import android.content.Intent;

/**
 * Created by lgallard on 8/20/15.
 */
public class UtorrentSession {

    private String token;
    private String cookie;
    private Intent intent;

    public UtorrentSession(String token, String cookie, Intent intent) {
        this.token = token;
        this.cookie = cookie;
        this.intent = intent;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
