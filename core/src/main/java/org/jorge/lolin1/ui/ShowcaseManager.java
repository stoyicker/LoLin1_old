package org.jorge.lolin1.ui;

import android.app.Activity;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano on 11/06/14.
 */
public abstract class ShowcaseManager {

    private static int THEME = -1;

    /**
     * @param themeRes If -1, the default theme is used.
     */
    public static void setTheme(int themeRes) {
        THEME = themeRes;
    }

    public static void createHomeOverlay(Activity activity, int titleRes, int contentRes, Boolean blockInteractions) {
        createHomeOverlay(activity, titleRes, contentRes, blockInteractions, null);
    }

    public static void createHomeOverlay(Activity activity, int titleRes, int contentRes, Boolean blockInteractions, View.OnClickListener listener) {
        createOverlayOnTarget(activity, titleRes, contentRes, new ActionViewTarget(activity, ActionViewTarget.Type.HOME), blockInteractions, listener);
    }

    /**
     * Blocks interactions by default.
     */
    public static void createHomeOverlay(Activity activity, int titleRes, int contentRes, View.OnClickListener listener) {
        createHomeOverlay(activity, titleRes, contentRes, Boolean.TRUE, listener);
    }

    public static void createOverlay(Activity activity, int titleRes, int contentRes, View targetView, Boolean blockInteractions) {
        createOverlay(activity, titleRes, contentRes, targetView, blockInteractions, null);
    }

    public static void createOverlay(Activity activity, int titleRes, int contentRes, View targetView, Boolean blockInteractions, View.OnClickListener listener) {
        createOverlayOnTarget(activity, titleRes, contentRes, new ViewTarget(targetView.getId(), activity), blockInteractions, listener);
    }

    /**
     * Blocks interactions by default.
     */
    public static void createOverlay(Activity activity, int titleRes, int contentRes, View targetView, View.OnClickListener listener) {
        createOverlay(activity, titleRes, contentRes, targetView, Boolean.TRUE, listener);
    }

    private static void createOverlayOnTarget(Activity activity, int titleRes, int contentRes, Target target, Boolean blockInteractions, View.OnClickListener listener) {
        ShowcaseView.Builder builder = new ShowcaseView.Builder(activity).setTarget(target).setContentTitle(titleRes).setContentText(contentRes);
        if (THEME != -1)
            builder.setStyle(THEME);
        if (!blockInteractions)
            builder.doNotBlockTouches();
        if (listener != null)
            builder.setOnClickListener(listener);
        builder.build();
    }

}
