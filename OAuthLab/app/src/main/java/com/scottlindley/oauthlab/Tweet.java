package com.scottlindley.oauthlab;

/**
 * Created by Scott Lindley on 11/17/2016.
 */

public class Tweet {
    private String text, created_at;

    public Tweet(String text, String created_at) {
        this.text = text;
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public String getCreated_at() {
        return created_at;
    }
}
