package com.example.mutidemo.widget.slidemenu;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/27 18:25
 */
public interface OnDragStateChangeListener {
    /**
     * 打开回调
     */
    void onOpen();

    /**
     * 关闭回调
     */
    void onClose();

    /**
     * 拖拽中回调
     *
     * @param direction
     */
    void onDrag(float direction);
}
