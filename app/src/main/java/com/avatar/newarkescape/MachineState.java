package com.avatar.newarkescape;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chx on 2016/12/16.
 */

public class MachineState implements Serializable {
    public enum State {
        CLOSED, MIDDLE, OPENED, CLOSING, OPENING, INIT
    }

    public enum DoorState {
        COMPRESSING, DECOMPRESSING, OPENED, CLOSED
    }
    
    //state
    private float mCO2Density;

    private State mEscapeState;
    private State mScuttleState;

    private boolean mLadderOpening, mLadderClosing;
    private boolean mLadderOpened, mLadderClosed;

    //warning
    private boolean mCO2DensityHigh1;
    private boolean mCO2DensityHigh2;
    private boolean mCO2DensityHigh3;
    private boolean mCO2SensorError;

    public void fillData(int start,int value) {
        switch (start) {
            case Constant.STATE_34:
                if ((value & Constant.VALUE_7_BIT) != 0) {
                    mScuttleState = State.CLOSED;
                }
                if ((value & Constant.VALUE_8_BIT) != 0) {
                    mScuttleState = State.MIDDLE;
                }
                if ((value & Constant.VALUE_9_BIT) != 0) {
                    mScuttleState = State.OPENED;
                }
                if ((value & Constant.VALUE_10_BIT) != 0) {
                    mEscapeState = State.MIDDLE;
                }
                if ((value & Constant.VALUE_11_BIT) != 0) {
                    mEscapeState = State.CLOSED;
                }
                if ((value & Constant.VALUE_12_BIT) != 0) {
                    mScuttleState = State.CLOSING;
                }
                if ((value & Constant.VALUE_13_BIT) != 0) {
                    mScuttleState = State.OPENING;
                }
                if ((value & Constant.VALUE_14_BIT) != 0) {
                    mEscapeState = State.CLOSING;
                }
                if ((value & Constant.VALUE_15_BIT) != 0) {
                    mEscapeState = State.OPENING;
                }
                break;
            case Constant.STATE_35:
                if ((value & Constant.VALUE_0_BIT) != 0) {
                    mEscapeState = State.INIT;
                }
                if ((value & Constant.VALUE_15_BIT) != 0) {
                    mLadderOpening = true;
                }
                break;
            case Constant.STATE_36:
                if ((value & Constant.VALUE_0_BIT) != 0) {
                    mLadderClosing = true;
                }
                break;
            case Constant.WARNING_33:
                if ((value & Constant.VALUE_3_BIT) != 0) {
                    mCO2DensityHigh1 = true;
                }
                if ((value & Constant.VALUE_4_BIT) != 0) {
                    mCO2DensityHigh2 = true;
                }
                if ((value & Constant.VALUE_5_BIT) != 0) {
                    mCO2DensityHigh3 = true;
                }
                if ((value & Constant.VALUE_10_BIT) != 0) {
                    mCO2SensorError = true;
                }
                break;
        }
    }

    public void setRealData(int start, float value) {
        switch (start) {
            case Constant.STATE_27:
                mCO2Density = value;
                break;

        }
    }

    public float getCO2Density() {
        return mCO2Density;
    }

    public State getEscapeState() {
        return mEscapeState;
    }

    public State getScuttleState() {
        return mScuttleState;
    }

    public boolean[] getLadderState() {
        return new boolean[]{mLadderOpening, mLadderClosing};
    }

    public String getEscapeStateString(State state, State scuttleState) {
        if (state == State.CLOSED) {
            return "关闭";
        } else if (state == State.MIDDLE) {
            return "逃生口中间位置";
        } else if (state == State.OPENED) {
            return "逃生口开启";
        } else if (state == State.CLOSING) {
            return "逃生口正在关闭";
        } else if (state == State.OPENING) {
            return "逃生口正在开启";
        } else if (state == State.INIT) {
            return "逃生口正在初始化位置";
        } else {
            if (scuttleState == State.CLOSED) {
                return "关闭";
            } else if (scuttleState == State.MIDDLE) {
                return "天窗处于中间位置";
            } else if (scuttleState == State.OPENING) {
                return "天窗正在开启";
            } else if (scuttleState == State.CLOSING) {
                return "天窗正在关闭";
            } else if (scuttleState == State.OPENED) {
                return "天窗开启";
            }
            return "error";
        }
    }


    public String getStateString(State state) {
        if (state == State.CLOSED) {
            return "关闭";
        } else if (state == State.MIDDLE) {
            return "中间位置";
        } else if (state == State.OPENED) {
            return "开启";
        } else if (state == State.CLOSING) {
            return "正在关闭";
        } else if (state == State.OPENING) {
            return "正在开启";
        } else if (state == State.INIT) {
            return "正在初始化位置";
        }
        return "error";
    }

    public ArrayList<Integer> getWarningStringIds() {
        ArrayList<Integer> warningStringIds = new ArrayList<>();
        if (mCO2SensorError) {
            warningStringIds.add(R.string.co2_sensor_error);
        }
        if (mCO2DensityHigh1) {
            warningStringIds.add(R.string.co2_density_high_1);
        }
        if (mCO2DensityHigh2) {
            warningStringIds.add(R.string.co2_density_high_2);
        }
        if (mCO2DensityHigh3) {
            warningStringIds.add(R.string.co2_density_high_3);
        }
        return warningStringIds;
    }

    public ArrayList<Integer> getPromptStringIds() {
        ArrayList<Integer> promptStringIds = new ArrayList<>();
        //escape-------------------------------------------------------------
        if (mEscapeState == State.OPENING) {
            promptStringIds.add(R.string.escape_opening);
        } else if (mEscapeState == State.CLOSING) {
            promptStringIds.add(R.string.escape_closing);
        } else if (mEscapeState == State.INIT) {
            promptStringIds.add(R.string.escape_init);
        }
        if (mScuttleState == State.OPENING) {
            promptStringIds.add(R.string.sunfoor_opening);
        } else if (mScuttleState == State.CLOSING) {
            promptStringIds.add(R.string.sunfoor_closing);
        }
        if (mLadderOpening) {
            promptStringIds.add(R.string.ladder_opening);
        } else if (mLadderClosing) {
                promptStringIds.add(R.string.ladder_closing);
        }
        return promptStringIds;
    }

    public boolean[] getEnvironmentWarnings() {
        return new boolean[] {
                mCO2DensityHigh1,
                mCO2DensityHigh2,
                mCO2DensityHigh3
        };
    }

    public boolean[] getEscapeStatus() {
        return new boolean[] {
                mScuttleState == State.CLOSED,
                mScuttleState == State.MIDDLE,
                mScuttleState == State.OPENED,
                mEscapeState == State.MIDDLE,
                mEscapeState == State.OPENED,
                mScuttleState == State.OPENING,
                mScuttleState == State.CLOSING,
                mEscapeState == State.OPENING,
                mEscapeState == State.CLOSING,
                mEscapeState == State.INIT
        };
    }
}
