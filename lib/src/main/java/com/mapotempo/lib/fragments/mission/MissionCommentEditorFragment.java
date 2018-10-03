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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

public class MissionCommentEditorFragment extends Fragment
{

    private MapotempoFleetManager mapotempoFleetManager;
    private Mission mMission;
    private TextInputEditText mComment;

    public MissionCommentEditorFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final String mission_id = getActivity().getIntent().getStringExtra("mission_id");
        mapotempoFleetManager = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        mMission = mapotempoFleetManager.getMissionAccess().get(mission_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_mission_comment_editor, container, false);
        mComment = view.findViewById(R.id.comment);
        mComment.setText(mMission.getSurveyComment() != null ? mMission.getSurveyComment() : "");
        return view;
    }

    /**
     * Save the current modifications as Survey Comment
     */
    public void saveComment()
    {
        String comment = mComment.getText()
            .toString()
            .trim();
        if (!comment.equals(mMission.getSurveyComment()))
        {
            mMission.setSurveyComment(comment);
            mMission.save();
        }
    }

    /**
     * Reset the current survey Comment by removing it from the survey model.
     */
    public void clearComment()
    {
        mMission.clearSurveyComment();
        mMission.save();
        mComment.setText("");
    }
}
