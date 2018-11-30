package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.utils.EasyGlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * author:  ycl
 * date:  2018/11/29 17:32
 * desc:
 */
public class GroupFilter extends AFilter {

    private Queue<AFilter> mFilterQueue;
    private List<AFilter> mFilters;
    private int size = 0;
    private int width = 0, height = 0;

    // 创建离屏buffer
    private int ftextureSize = 2;
    private int[] fFrame = new int[1];
    private int[] frender = new int[1];
    private int[] ftexture = new int[ftextureSize];
    private int textureIndex = 0;


    public GroupFilter(Resources mRes) {
        super(mRes);
        mFilters = new ArrayList<>();
        mFilterQueue = new ConcurrentLinkedQueue<>();//为何用这个queue队列
    }


    public boolean addFilter(AFilter filter) {
        boolean b = mFilters.remove(filter);
        if (b) {
            size--;
        }
        return b;
    }

    public boolean removeFilter(AFilter filter) {
        boolean b = mFilters.remove(filter);
        if (b) {
            size--;
        }
        return b;
    }

    public AFilter removeFilter(int index) {
        AFilter f = mFilters.remove(index);
        if (f != null) {
            size--;
        }
        return f;
    }

    public void clearAll() {
        mFilterQueue.clear();
        mFilters.clear();
        size = 0;
    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;

        updateFilter();
        createFrameBuffer();
    }

    @Override
    public void draw() {

    }

    private void updateFilter() {
        AFilter f;
        while ((f = mFilterQueue.poll()) != null) {
            f.create();
            f.setSize(width, height);

            mFilters.add(f);
            size++;
        }
    }


    private void createFrameBuffer() {
        // 创建
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, frender, 0);

        EasyGlUtils.genTexturesWithParameter(ftextureSize, ftexture,
                0, GLES20.GL_RGBA, width, height);
        // 绑定
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, frender[0]);

        // 放置数据到缓冲区
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_TEXTURE_2D, ftexture[0], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, frender[0]);

        // 解绑
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, frender[0]);

        // 删除缓冲的节点
        GLES20.glDeleteTextures(ftextureSize, ftexture, 0);
        GLES20.glDeleteRenderbuffers(1, frender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
    }
}
