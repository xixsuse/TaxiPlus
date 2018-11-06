package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization.AuthSecondStepFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class WebFragment extends Fragment {
    public static final String TAG = Constants.WEBFRAGMENT;
    private static final String URL = "url";

    private ImageButton back;
    private WebView webView;

    private String url;

    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        back = view.findViewById(R.id.fw_back);
        webView = view.findViewById(R.id.fw_webview);


        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}
