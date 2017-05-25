package com.avatar.newarkescape;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Created by chx on 2016/12/22.
 */

public abstract class BaseFragment extends Fragment {
    private Logger mLogger = LoggerFactory.getLogger(BaseFragment.class);

    protected static final String STATUS_PASSWORD = "newarkadmin";
    private Typeface mChineseTypeface, mEnglishTypeface;
    protected ImageView mCO2DensityImage;

    protected TextView mCO2DensityText, mCO2DensityUnitText;

    protected ImageView mSlideView;

    protected ImageView mLifeSmogWarningImage, mDeviceSmogWarningImage;
    protected TextView mLifeSmogWarningText, mDeviceSmogWarningText;

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetManager mgr = getActivity().getAssets();
        mChineseTypeface = Typeface.createFromAsset(mgr, "fonts/chinese.ttf");
        mEnglishTypeface = Typeface.createFromAsset(mgr, "fonts/GUN4FC.TTF");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (Communicator.ACTION_STATE.equalsIgnoreCase(action)) {
                        MachineState state = (MachineState) intent.getSerializableExtra(Communicator.FLAG_GET_STATE);
                        onUpdateMachineState(state);
                    } else if (MainActivity.ACTION_SLIDE_START.equalsIgnoreCase(action)) {
                        mSlideView.setSelected(true);
                    } else if (MainActivity.ACTION_SLIDE_END.equalsIgnoreCase(action)) {
                        mSlideView.setSelected(false);
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter(Communicator.ACTION_STATE);
        filter.addAction(MainActivity.ACTION_SLIDE_START);
        filter.addAction(MainActivity.ACTION_SLIDE_END);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }

    public abstract void onUpdateMachineState(MachineState state);

    public void setTextToChinese() {
//        updateLanguage(Locale.SIMPLIFIED_CHINESE);
        setTextSub(getView(), mChineseTypeface);
    }

    public void setTextToEnglish() {
//        updateLanguage(Locale.ENGLISH);
        setTextSub(getView(), mEnglishTypeface);
    }

    private void setTextSub(View view, Typeface typeface) {
        if (view instanceof TextView) {
            String s = (String) view.getTag();
            if (!(s != null && s.equalsIgnoreCase("N"))) {
                view.requestLayout();
                ((TextView) view).setTypeface(typeface);
                if (view instanceof AppTextView) {
                    ((AppTextView) view).reLoadLanguage();
                }
//                ((TextView) view).setTextLocale(locale);
//                view.invalidate();
            }
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setTextSub(((ViewGroup) view).getChildAt(i), typeface);
            }
        }
    }

    public void setTextFont(TextView view) {
        Configuration config = getResources().getConfiguration();
        String language = config.locale.getLanguage();
        if (language.endsWith("zh")) {
            view.setTypeface(mChineseTypeface);
        } else {
            view.setTypeface(mEnglishTypeface);
        }
    }

    public void initText() {
        setShadow(getView());
//        setTextToChinese();
        Configuration config = getResources().getConfiguration();
        String language = config.locale.getLanguage();
        if (language.endsWith("zh")) {
            setTextToChinese();
        } else {
            setTextToEnglish();
        }
    }

    private void setShadow(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setLetterSpacing(0.1f);
            String s = (String) view.getTag();
            ((TextView) view).setTypeface(mEnglishTypeface);
            if (s == null || !s.equalsIgnoreCase("S")) {
                ((TextView) view).setShadowLayer(4F, 0F,0F, 0xFF63D7FF);
            }
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setShadow(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    public void changeLanguage() {
        Configuration config = getResources().getConfiguration();
        String language = config.locale.getLanguage();
        if (language.endsWith("zh")) {
            config.setLocale(Locale.ENGLISH);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            ((MainActivity) getActivity()).updateLanguage(false);
        } else {
            config.setLocale(Locale.SIMPLIFIED_CHINESE);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            ((MainActivity) getActivity()).updateLanguage(true);
        }
    }

    public void setCO2Density(float f) {
        mCO2DensityText.setText(FormatUtils.float2String(f + 0.5f));
        if (f < 2000) {
            mCO2DensityText.setTextColor(getResources().getColor(R.color.text_green));
            mCO2DensityUnitText.setTextColor(getResources().getColor(R.color.text_green));
            mCO2DensityImage.setImageResource(R.drawable.co2_green);
        } else if (f < 4000) {
            mCO2DensityText.setTextColor(getResources().getColor(R.color.text_blue));
            mCO2DensityUnitText.setTextColor(getResources().getColor(R.color.text_blue));
            mCO2DensityImage.setImageResource(R.drawable.co2_blue);
        } else if (f < 8000) {
            mCO2DensityText.setTextColor(getResources().getColor(R.color.text_orange));
            mCO2DensityUnitText.setTextColor(getResources().getColor(R.color.text_orange));
            mCO2DensityImage.setImageResource(R.drawable.co2_orange);
        } else {
            mCO2DensityText.setTextColor(getResources().getColor(R.color.text_red));
            mCO2DensityUnitText.setTextColor(getResources().getColor(R.color.text_red));
            mCO2DensityImage.setImageResource(R.drawable.co2_red);
        }
    }
}
