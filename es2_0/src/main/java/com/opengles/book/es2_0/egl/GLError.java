package com.opengles.book.es2_0.egl;

/**
 * author: ycl
 * date: 2018-11-25 11:25
 * desc:
 */
public enum GLError {
    OK(0, "ok"),
    ConfigErr(1, "config not support");
    private int code;
    private String msg;

    GLError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "GLError{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
