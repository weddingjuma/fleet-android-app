package mapotempo.com.mapotempo_fleet_android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mapotempo.fleet.api.model.MissionInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * SignatureActivity.
 */
public class SignatureActivity extends AppCompatActivity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private MissionInterface mMission = null;

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String mission_id = getIntent().getStringExtra("mission_id");
        mMission = ((MapotempoApplication) getApplication()).getManager().getMissionAccess().get(mission_id);

        setContentView(R.layout.activity_signature);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
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

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

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
                if (mMission != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    ByteArrayInputStream bi = new ByteArrayInputStream(stream.toByteArray());
                    mMission.setAttachment("signature", "image/jpeg", bi);
                    mMission.save();
                    Toast.makeText(SignatureActivity.this, R.string.save_signature, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
}
