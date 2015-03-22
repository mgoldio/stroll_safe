package edu.illinois.strollsafe.util;

import android.location.Criteria;

/**
 * @author MichaelGoldstein
 */
public class GeneralSingletons {

    public static final Runnable EMPTY_RUNNABLE = new Runnable() {

        @Override
        public void run() {

        }
    };

    public static final Criteria LOOSE_CRITERIA = new Criteria() {{
        setAccuracy(Criteria.ACCURACY_LOW);
    }};
}
