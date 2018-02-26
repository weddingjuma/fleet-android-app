/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package com.mapotempo.lib.fragments.mission;

import android.support.v4.view.ViewPager;
import android.view.View;
import com.mapotempo.lib.R;

public class MissionsPagerBorderTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        float margin = view.getResources().getDimension(R.dimen.margin_on_scroll);

        if (position < 0 && position > -1) { // [LEFT]
            view.setTranslationX(-margin * Math.abs(position));
        } else if (position < 1 && position > 0) { // [RIGHT]
            view.setTranslationX(margin * Math.abs(position));
        }

        if (position == 0 || position < -1 || position > 1) {
            view.setTranslationX(0f);
        }
    }
}
