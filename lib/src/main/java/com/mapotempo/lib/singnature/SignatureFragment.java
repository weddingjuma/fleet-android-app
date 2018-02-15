package com.mapotempo.lib.singnature;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mapotempo.fleet.api.model.MissionInterface;

import mapotempo.com.lib.R;

public class SignatureFragment extends DialogFragment {

    public static SignatureFragment newInstance() {
        SignatureFragment f = new SignatureFragment();
        return f;
    }

    public interface SignatureSaveListener {
        boolean onSignatureSave(Bitmap signatureBitmap);
    }

    public void setSignatureSaveListener(SignatureSaveListener signatureSaveListener) {
        mSignatureSaveListener = signatureSaveListener;
    }

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private MissionInterface mMission = null;
    private Context mContext = null;
    private SignatureSaveListener mSignatureSaveListener = new SignatureSaveListener() {
        @Override
        public boolean onSignatureSave(Bitmap signatureBitmap) {
            return true;
        }
    };

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signature, container, false);
        mSignaturePad = v.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = v.findViewById(R.id.clear_button);
        mSaveButton = v.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if (mSignatureSaveListener.onSignatureSave(signatureBitmap)) {
                    dismiss();
                }
            }
        });
        return v;
    }
}
