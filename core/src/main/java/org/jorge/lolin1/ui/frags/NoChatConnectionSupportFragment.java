package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.ChatOverviewActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

public class NoChatConnectionSupportFragment extends Fragment {

    private ChatOverviewActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChatOverviewActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_chat_overview_no_connection, container,
                Boolean.FALSE);
        final Context context = mActivity.getApplicationContext();
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoLin1Utils.isInternetReachable(context)) {
                    mActivity.requestProtocolReInit();
                } else {
                    Toast.makeText(context,
                            R.string.error_no_connection,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return ret;
    }
}
