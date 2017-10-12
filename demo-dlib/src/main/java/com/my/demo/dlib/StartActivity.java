// Copyright (c) 2016-present boyw165
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.my.demo.dlib;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.my.core.protocol.IProgressBarView;
import com.my.core.util.ViewUtil;
import com.my.widget.adapter.SampleMenuAdapter;
import com.my.widget.adapter.SampleMenuAdapter.SampleMenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StartActivity
    extends AppCompatActivity
    implements IProgressBarView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.menu)
    ListView mStartMenu;

    // Butter Knife.
    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        // Init view binding.
        mUnbinder = ButterKnife.bind(this);

        // Toolbar.
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        // List menu.
        mStartMenu.setAdapter(onCreateSampleMenu());
        mStartMenu.setOnItemClickListener(onClickSampleMenuItem());
    }

    @Override
    public void showProgressBar() {
        ViewUtil
            .with(this)
            .setProgressBarCancelable(false)
            .showProgressBar(getString(R.string.loading));
    }

    @Override
    public void showProgressBar(String msg) {
        showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        ViewUtil
            .with(this)
            .hideProgressBar();
    }

    @Override
    public void updateProgress(int progress) {
        showProgressBar();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }

    @SuppressWarnings({"unchecked"})
    protected SampleMenuAdapter onCreateSampleMenu() {
        return new SampleMenuAdapter(
            this,
            new SampleMenuItem[]{
                new SampleMenuItem(
                    "Detection using DLib",
                    "There're two steps of a complete face landmarks detection:\n" +
                    "(1) Detect face boundaries.\n" +
                    "(2) Given the face boundaries, align the landmarks to the " +
                    "faces.\n" +
                    "Two parts are done by using DLib.\n" +
                    "(about 10fps)",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                new Intent(StartActivity.this,
                                           SampleOfFacesAndLandmarksActivity1.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        }
                    }),
                new SampleMenuItem(
                    "Detection using Google Vision and DLib",
                    "There're two steps of a complete face landmarks detection:\n" +
                    "(1) Detect face boundaries.\n" +
                    "(2) Given the face boundaries, align the landmarks to the " +
                    "faces.\n" +
                    "Use 3rd party faces detection to boost finding the face " +
                    "rectangles. Given the face rectangles, use DLib to do " +
                    "landmarks alignment.\n" +
                    "(about 30fps)",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkPlayServices()) {
                                startActivity(
                                    new Intent(StartActivity.this,
                                               SampleOfFacesAndLandmarksActivity2.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            } else {
                                new AlertDialog.Builder(StartActivity.this)
                                    .setTitle(R.string.alert_unsupported_api)
                                    .setMessage(R.string.alert_no_google_play_service)
                                    .show();
                            }
                        }
                    }),
                new SampleMenuItem(
                    "Camera2 API Experiment",
                    "Exploring what is special of using Camera2 versus " +
                    "depreciated Camera",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startActivity(
                                    new Intent(StartActivity.this,
                                               SampleOfCamera2ApiActivity1.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            } else {
                                new AlertDialog.Builder(StartActivity.this)
                                    .setTitle(R.string.alert_unsupported_api)
                                    .setMessage(R.string.alert_api_21_is_required)
                                    .show();
                            }
                        }
                    })
            });
    }

    protected AdapterView.OnItemClickListener onClickSampleMenuItem() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                final SampleMenuItem item = (SampleMenuItem) parent.getAdapter()
                                                                   .getItem(position);
                item.onClickListener.onClick(view);
            }
        };
    }

    /**
     * Check the device to make sure it has the Google Play Services APK.
     */
    protected boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 0)
                               .show();
            }
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////
}
