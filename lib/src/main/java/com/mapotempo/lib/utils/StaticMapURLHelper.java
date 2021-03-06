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

package com.mapotempo.lib.utils;

import android.util.Log;

import java.util.Locale;

public class StaticMapURLHelper
{

    /**
     * Tilehosting URL builder
     * => https://maps.tilehosting.com/styles/basic/static/{lon},{lat},{zoom}/{width}x{height}[@2x].png?key=LLRxrAW8qh4LHSzTw6qo
     */
    public static class TileHostingURLBuilder
    {
        private String mBaseURL = "https://maps.tilehosting.com";
        private String mStyle = "basic";
        private Double mLon = 0.;
        private Double mLat = 0.;
        private Integer mZoom = 0;
        private Integer mWidth = 0;
        private Integer mHeight = 0;
        private String mKey = "";

        public String getBaseURL()
        {
            return mBaseURL;
        }

        public TileHostingURLBuilder setBaseURL(String baseURL)
        {
            this.mBaseURL = baseURL;
            return this;
        }

        public String getStyle()
        {
            return mStyle;
        }

        public TileHostingURLBuilder setStyle(String style)
        {
            this.mStyle = style;
            return this;
        }

        public Double getLon()
        {
            return mLon;
        }

        public TileHostingURLBuilder setLon(Double lon)
        {
            this.mLon = lon;
            return this;
        }

        public Double getLat()
        {
            return mLat;
        }

        public TileHostingURLBuilder setLat(Double lat)
        {
            this.mLat = lat;
            return this;
        }

        public Integer getZoom()
        {
            return mZoom;
        }

        public TileHostingURLBuilder setZoom(Integer zoom)
        {
            this.mZoom = zoom;
            return this;
        }

        public Integer getWidth()
        {
            return mWidth;
        }

        public TileHostingURLBuilder setWidth(Integer width)
        {
            this.mWidth = width;
            return this;
        }

        public Integer getHeight()
        {
            return mHeight;
        }

        public TileHostingURLBuilder setHeight(Integer height)
        {
            this.mHeight = height;
            return this;
        }

        public String getKey()
        {
            return mKey;
        }

        public TileHostingURLBuilder setKey(String key)
        {
            this.mKey = key;
            return this;
        }

        public String Build()
        {
            String res = String.format(
                Locale.US,
                "%s/styles/%s/static/%f,%f,%d/%dx%d.png?key=%s",
                mBaseURL,
                mStyle,
                mLon,
                mLat,
                mZoom,
                mWidth,
                mHeight,
                mKey);
            Log.d(getClass().getName(), res);
            return res;
        }
    }
}
