package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class AgreementFragment extends Fragment {
    public static final String TAG = Constants.AGREEMENTFRAGMENTTAG;

    private WebView webView;
    private ImageButton backView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agreement, container, false);
        initViews(view);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://api.priceclick.kz/terms");

        return view;
    }

    private void initViews(View view){
        webView = view.findViewById(R.id.fa_webview);
        backView = view.findViewById(R.id.fa_back_view);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

}
