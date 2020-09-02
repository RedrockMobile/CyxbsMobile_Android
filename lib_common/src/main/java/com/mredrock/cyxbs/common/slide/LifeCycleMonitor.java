package com.mredrock.cyxbs.common.slide;

/**
 * Interface that monitor activity or fragment life-cycle events.
 */
public interface LifeCycleMonitor {

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    class Stub implements LifeCycleMonitor {

        @Override
        public void onResume() {
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onDestroy() {
        }
    }

}
