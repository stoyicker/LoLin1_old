package org.jorge.lolin1.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ReflectedRes {
    private ReflectedRes() {
    }

    public static String string(Context context, String variableName) {
        return context.getResources().getText(getResourceId(variableName, String.class)).toString();
    }

    public static Drawable drawable(Context context, String variableName) {
        return context.getResources().getDrawable(getResourceId(variableName, Drawable.class));
    }

    /**
     * Provides the id of a resource.
     *
     * @param variableName The name of the resource.
     * @param resourceType The class of the type of the resource.
     * @return The id of the requested resource or -1 if any error happens.
     */
    private static int getResourceId(String variableName, Class<?> resourceType) {
        int ret = -1;
        try {
            ret = resourceType.getDeclaredField(variableName).getInt(variableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}