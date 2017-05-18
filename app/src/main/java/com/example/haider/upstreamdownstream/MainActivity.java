package com.example.haider.upstreamdownstream;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    PNConfiguration pnConfiguration ;
    PubNub pubnub;
    Button btn;
    String deviceId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Toast.makeText(this, "Device ID "+deviceId, Toast.LENGTH_SHORT).show();
        btn = (Button) findViewById(R.id.sendbtn);
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey("pub-c-c4588c05-0122-41b6-a496-3e5656c3d024");
        pnConfiguration.setSubscribeKey("sub-c-87a42fd0-2016-11e7-bb8a-0619f8945a4f");

        pubnub = new PubNub(pnConfiguration);


        pubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Arrays.asList("demochannel"))
                .deviceId(deviceId).
                async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        // handle response.
                        if(status.isError()){
                            Toast.makeText(MainActivity.this, "Cannot add Notification", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Notification is Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubNub, PNStatus pnStatus) {
                Toast.makeText(MainActivity.this, "inside status", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void message(PubNub pubNub, PNMessageResult pnMessageResult) {


            }
            @Override
            public void presence(PubNub pubNub, PNPresenceEventResult pnPresenceEventResult) {

            }
        });
        pubnub.subscribe().channels(Arrays.asList("demochannel")).execute();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SendMessage();
                Checkingpayload();
            }
        });


    }


    public void Checkingpayload(){
/*
//        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
//        String s = "{\"data\" :{\"a\" : \"1\"}";
            try {
            obj2.put("a","2");
            obj1.putOpt("data",obj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("a", "1");

        Map<String, Object> googlePayload = new HashMap<>();
        googlePayload.put("data", dataPayload);

        Map<String, Object> payload = new HashMap<>();
//        payload.put("pn_apns", <apple Payload>);
//        payload.put("pn_mpns", <microsoft payload>);

        payload.put("pn_gcm", googlePayload);


        pubnub.publish()
                .message(payload)
                .channel("demochannel")
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        /// handle publish result.
                        if(status.isError()){
                            Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Payload Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void SendMessage(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("text","Demo Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish().channel("demochannel").message(obj).usePOST(true).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult pnPublishResult, PNStatus pnStatus) {
                if(pnStatus.isError()){
                    Toast.makeText(MainActivity.this, "Error Sending", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
