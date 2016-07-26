package com.lody.virtual.client.hook.patchs.notification.compat;

import android.app.PendingIntent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.lody.virtual.helper.utils.Reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * Remoteviews的点击事件
 *
 * @author 247321453
 */
class PendIntentCompat {
    private RemoteViews mRemoteViews;
    private Map<Integer, PendingIntent> clickIntents;
    private boolean isBig;

    public PendIntentCompat(RemoteViews mRemoteViews, boolean isBig) {
        this.mRemoteViews = mRemoteViews;
        this.isBig = isBig;
    }

    public int findPendIntents() {
        if (clickIntents == null) {
            clickIntents = getClickIntents(mRemoteViews);
        }
        return clickIntents.size();
    }

    /***
     *
     * @param remoteViews 当前
     * @param remoteview 当前remoteviews的view
     * @param oldRemoteView 旧的view
     */
    public void setPendIntent(RemoteViews remoteViews,View remoteview,View oldRemoteView) {
        if (findPendIntents() > 0) {
            //view2+clickIntents=>区域和事件
            Iterator<Map.Entry<Integer, PendingIntent>> set = clickIntents.entrySet().iterator();
            List<RectInfo> list = new ArrayList<>();
            //区域对应点击事件
            while (set.hasNext()) {
                Map.Entry<Integer, PendingIntent> e = set.next();
                View view = oldRemoteView.findViewById(e.getKey());
                if (view != null) {
                    Rect rect = new Rect();
                    view.getHitRect(rect);
                    list.add(new RectInfo(rect, e.getValue()));
                }
            }
            //根据区域查找id，设置点击事件
            if (remoteview instanceof ViewGroup) {
                setIntentByViewGroup(remoteViews, (ViewGroup) remoteview, list);
            }
        }
    }

    private void setIntentByViewGroup(RemoteViews remoteViews, ViewGroup viewGroup, List<RectInfo> list) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                //linearlayout
                setIntentByViewGroup(remoteViews, (ViewGroup) v, list);
            } else if (v instanceof TextView) {
                //textview
                Rect rect = new Rect();
                v.getHitRect(rect);
                //height修正
                rect.top += viewGroup.getTop();
                rect.bottom += viewGroup.getTop();
//                Log.d("kk", v.getId() + ",rect=" + rect);
                PendingIntent pendingIntent = findIntent(rect, list);
                if (pendingIntent != null) {
//                    Log.d("kk", v.getId() + " set click =" + pendingIntent.getIntent().getParcelableExtra(ExtraConstants.EXTRA_INTENT));
                    remoteViews.setOnClickPendingIntent(v.getId(), pendingIntent);
                }
            }
        }
    }

    private PendingIntent findIntent(Rect rect, List<RectInfo> list) {
        //rect在哪一个占面积最大的点击事件上面,则设置事件
        int maxArea = 0;
        PendingIntent maxIntent = null;
        for (RectInfo rectInfo : list) {
            int size = getOverlapArea(rect, rectInfo.rect);
            if (size > maxArea) {
                if (size == 0) {
                    Log.w("kk", "find two:" + rectInfo.rect);
                }
                maxArea = size;
                maxIntent = rectInfo.mPendingIntent;
            }
        }
        return maxIntent;
    }

    private int getOverlapArea(Rect rect1, Rect rect2) {
        //2个区域重叠的面积
        Rect rect = new Rect();
        rect.left = Math.max(rect1.left, rect2.left);
        rect.top = Math.max(rect1.top, rect2.top);
        rect.right = Math.min(rect1.right, rect2.right);
        rect.bottom = Math.min(rect1.bottom, rect2.bottom);
        if (rect.left < rect.right && rect.top < rect.bottom) {
            return (rect.right - rect.left) * (rect.bottom - rect.top);
        }
        return 0;
    }

    /**
     * id和点击事件intent
     */
    private Map<Integer, PendingIntent> getClickIntents(RemoteViews remoteViews) {
        Map<Integer, PendingIntent> map = new HashMap<>();
        if (remoteViews == null) return map;
        Object mActionsObj = null;
        try {
            mActionsObj = Reflect.on(remoteViews).get("mActions");
        } catch (Exception e) {

        }
        if (mActionsObj == null) {
            return map;
        }
        if (mActionsObj instanceof Collection) {
            Collection mActions = (Collection) mActionsObj;
            Iterator iterable = mActions.iterator();
            while (iterable.hasNext()) {
                Object object = iterable.next();
                if (object != null) {
                    String action;
                    try {
                        action = Reflect.on(object).call("getActionName").get();
                    } catch (Exception e) {
                        action = object.getClass().getSimpleName();
                    }
                    if ("SetOnClickPendingIntent".equalsIgnoreCase(action)) {
                        int id = Reflect.on(object).get("viewId");
                        PendingIntent intent = Reflect.on(object).get("pendingIntent");
                        map.put(id, intent);
                    }
                }
            }
        }
        return map;
    }

    class RectInfo {
        public RectInfo(Rect rect, PendingIntent pendingIntent) {
            this.rect = rect;
            mPendingIntent = pendingIntent;
        }

        Rect rect;
        PendingIntent mPendingIntent;
    }
}
