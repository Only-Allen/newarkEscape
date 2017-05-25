package com.avatar.newarkescape;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chx on 2016/11/21.
 */

public class ControllerFragment extends BaseFragment implements View.OnClickListener {
    private static Logger mLogger = LoggerFactory.getLogger(ControllerFragment.class);
    private Communicator mCommunicator;

    //escape
    private TextView mScuttleImage, mEscapeImage, mEscapeCloseImage;

    //ladder
    private TextView mLadderUp, mLadderDown;

    //model image
    private ImageView mScuttleModelImage, mLadderImage;

    private List<Integer> mPromptStringIds, mWarningStringIds;
    private static final int PROMPT_NUMBER = 5;//提示的最大条数
    private static final int WARNING_NUMBER = 5;//警告的最大条数

    private LinearLayout mPromptContainer, mWarningContainer;

    private TextView mNetUnavailableText;
    private int mIndexOfString;
    private int mNumberOfNull;
    private static final int DELAY_CONNECTING_NET_TEXT_CHANGE = 1000;
    private static final int NUMBER_OF_NULL_AS_DISCONNECT = 10;

    private boolean mShouldIgnoreState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommunicator = Communicator.getInstance();
        mPromptStringIds = new ArrayList<>();
        mWarningStringIds = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_controller, container, false);
        initView(v);
        v.post(new Runnable() {
            @Override
            public void run() {
                initText();
                mCommunicator.startCommunicate(getActivity());
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initView(View v) {
//        mWaterRestText = (TextView) v.findViewById(R.id.controller_water_rest_text);
        mCO2DensityText = (TextView) v.findViewById(R.id.controller_co2_density_text);
        mCO2DensityUnitText = (TextView) v.findViewById(R.id.controller_co2_density_unit_text);
        mCO2DensityImage = (ImageView) v.findViewById(R.id.controller_co2_density_image);
        mScuttleImage = (TextView) v.findViewById(R.id.controller_scuttle);
        mEscapeImage = (TextView) v.findViewById(R.id.controller_escape);
        mEscapeCloseImage = (TextView) v.findViewById(R.id.controller_escape_close);
        mLadderUp = (TextView) v.findViewById(R.id.controller_up);
        mLadderDown = (TextView) v.findViewById(R.id.controller_down);
        mLadderImage = (ImageView) v.findViewById(R.id.controller_ladder_icon);
        mScuttleImage.setOnClickListener(this);
        mEscapeImage.setOnClickListener(this);
        mEscapeCloseImage.setOnClickListener(this);
        mLadderUp.setOnClickListener(this);
        mLadderDown.setOnClickListener(this);
        v.findViewById(R.id.controller_language).setOnClickListener(this);
        v.findViewById(R.id.controller_reset).setOnClickListener(this);
        mScuttleModelImage = (ImageView) v.findViewById(R.id.control_scuttle);

        mWarningContainer = (LinearLayout) v.findViewById(R.id.controller_warning_container);
        mPromptContainer = (LinearLayout) v.findViewById(R.id.controller_prompt_container);

        mPromptContainer.post(new Runnable() {
            @Override
            public void run() {
                onUpdateMachineState(new MachineState());
            }
        });
        //just to see UI, will be deleted soon
//        v.post(new Runnable() {
//            @Override
//            public void run() {
//                setWaterRest(70);
//                setMode(1);
//                setLightState(true, true, true, false);
//                setWindowsState(true, true, true, false);
//            }
//        });
    }

    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 100);
        mShouldIgnoreState = true;
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                mShouldIgnoreState = false;
            }
        }, 300);
        mCommunicator.reStartCommunicate();
        switch (v.getId()) {
            case R.id.controller_language:
                changeLanguage();
                break;
            case R.id.controller_scuttle:
                mCommunicator.openScuttle();
                break;
            case R.id.controller_escape:
                mCommunicator.openEscape();
                break;
            case R.id.controller_escape_close:
                mCommunicator.closeEscape();
                break;
            case R.id.controller_reset:
                mCommunicator.resetEscapeWarning();
                break;
            case R.id.controller_up:
                mCommunicator.upLadder();
                break;
            case R.id.controller_down:
                mCommunicator.downLadder();
                break;
            default:
        }
        mCommunicator.reStartCommunicate();
    }

/**************************************    UI    *************************************************/

    @Override
    public void onUpdateMachineState(MachineState state) {
        if (state == null) {
            if (mNumberOfNull >= NUMBER_OF_NULL_AS_DISCONNECT) {
                showNetUnavailable();
            }
            mNumberOfNull++;
            return;
        }
        mNumberOfNull = 0;
        if (mShouldIgnoreState) {
            return;
        }
        setCO2Density(state.getCO2Density());
        setEscapeState(state.getEscapeState(), state.getScuttleState());
        fillPromptText(state);
        fillWarningText(state);
        showPromptAndWarning();
    }

    public void setEscapeState(MachineState.State escapeState, MachineState.State scuttleState) {
        mScuttleImage.setSelected(false);
        mEscapeImage.setSelected(false);
        mEscapeCloseImage.setSelected(false);
        mScuttleModelImage.setImageResource(R.drawable.scuttle_model);
        if (escapeState == MachineState.State.OPENED) {
            mEscapeImage.setSelected(true);
        } else {
            if (scuttleState == MachineState.State.OPENED) {
                mScuttleImage.setSelected(true);
            }
        }
        if (scuttleState == MachineState.State.CLOSED) {
            mEscapeCloseImage.setSelected(true);
            mScuttleModelImage.setImageBitmap(null);
        }
    }

    public void setLadderState() {

    }

    public TextView getPromptOrWarningTextView(int stringId, boolean warning) {
        AppTextView textView = new AppTextView(getActivity());
        textView.setTextById(stringId);
        textView.setTextSize(14);
        if (warning) {
            textView.setTextColor(getResources().getColor(R.color.text_red));
            textView.setBackgroundResource(R.drawable.warning_text_border);
        } else {
            textView.setTextColor(getResources().getColor(R.color.text_blue));
            textView.setBackgroundResource(R.drawable.prompt_text_border);
        }
        setTextFont(textView);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public void fillPromptText(MachineState state) {
        ArrayList<Integer> promptStringIds = state.getPromptStringIds();
        for (int i = 0; i < mPromptStringIds.size(); i++) {
            Integer prompt = mPromptStringIds.get(i);
            if (!promptStringIds.contains(prompt)) {
                mPromptStringIds.remove(prompt);
                i--;
            }
        }
        for (int i : promptStringIds) {
            if (!mPromptStringIds.contains(i)) {
                mPromptStringIds.add(0, i);
            }
        }

    }

    public void fillWarningText(MachineState state) {
        ArrayList<Integer> warningStringIds = state.getWarningStringIds();
        for (int i = 0; i < mWarningStringIds.size(); i++) {
            Integer warning = mWarningStringIds.get(i);
            if (!warningStringIds.contains(warning)) {
                mWarningStringIds.remove(warning);
                i--;
            }
        }
        for (int i : warningStringIds) {
            if (!mWarningStringIds.contains(i)) {
                mWarningStringIds.add(0, i);
            }
        }
    }

    public void showPromptAndWarning() {
        int promptNum = mPromptStringIds.size() > PROMPT_NUMBER ?
                PROMPT_NUMBER : mPromptStringIds.size();
        if (promptNum == 0) {
            mPromptContainer.setVisibility(View.GONE);
        } else {
            mPromptContainer.removeAllViews();
            for (int i = 0; i < promptNum; i++) {
                mPromptContainer.addView(getPromptOrWarningTextView(mPromptStringIds.get(i), false));
            }
            mPromptContainer.setVisibility(View.VISIBLE);
        }


        int warningNum = mWarningStringIds.size() > WARNING_NUMBER ?
                WARNING_NUMBER : mWarningStringIds.size();
        if (warningNum == 0) {
            mWarningContainer.setVisibility(View.GONE);
        } else {
            mWarningContainer.removeAllViews();
            for (int i = 0; i < warningNum; i++) {
                mWarningContainer.addView(getPromptOrWarningTextView(mWarningStringIds.get(i), true));
            }
            mWarningContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showNetUnavailable() {
        mWarningContainer.setVisibility(View.VISIBLE);
        mWarningContainer.removeAllViews();
        if (mNetUnavailableText == null) {
            mNetUnavailableText = getPromptOrWarningTextView(R.string.connecting_net, true);
            mIndexOfString = 0;
            mNetUnavailableText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mIndexOfString == 0) {
                        mIndexOfString = 1;
                        mNetUnavailableText.setText(R.string.connecting_net1);
                    } else if (mIndexOfString == 1) {
                        mIndexOfString = 2;
                        mNetUnavailableText.setText(R.string.connecting_net2);
                    } else {
                        mIndexOfString = 0;
                        mNetUnavailableText.setText(R.string.connecting_net);
                    }
                    mNetUnavailableText.postDelayed(this, DELAY_CONNECTING_NET_TEXT_CHANGE);
                }
            }, DELAY_CONNECTING_NET_TEXT_CHANGE);
        }
        mWarningContainer.addView(mNetUnavailableText);
    }

}