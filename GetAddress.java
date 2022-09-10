package com.kart.locationapp;

import android.app.IntentService;
import android.content.Intent;
import android.icu.util.ULocale;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GetAddress extends IntentService {

    private ResultReceiver resultReceiver;
    public GetAddress (){
        super("GetAddress");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            String errorMassage = "";
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if (location == null){
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {

                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            }catch (Exception e){
                e.printStackTrace();

            }if (addresses == null || addresses.isEmpty()){
                deliverResultToReceiver(Constants.FAILURE_RESULT,errorMassage);

            }else {
                Address address = addresses.get(0);
                ArrayList<String> adressFragments = new ArrayList<>();
                for (int i= 0; i <= address.getMaxAddressLineIndex();i++){
                    adressFragments.add(address.getAddressLine(i));
                }
                deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")),adressFragments));
            }

        }

    }
    private void deliverResultToReceiver(int resultCode,String addressMassage){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,addressMassage);
        resultReceiver.send(resultCode,bundle);

    }
}
