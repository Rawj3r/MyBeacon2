package com.equidais.mybeacon.apiservice;

import com.equidais.mybeacon.common.GlobalConst;
import com.equidais.mybeacon.model.LoginResult;
import com.equidais.mybeacon.model.MessageResult;
import com.equidais.mybeacon.model.VisitEntriesResult;
import com.equidais.mybeacon.model.VisitSummaryResult;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public class ApiClient {

    public static final String API_ROOT = GlobalConst.API_ROOT ;
    private static ApiInterface myBeaconService;


    public static ApiInterface getApiClient() {
        if (myBeaconService == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_ROOT)
                    .setClient(new OkClient(okHttpClient))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            myBeaconService = restAdapter.create(ApiInterface.class);
        }

        return myBeaconService;
    }


    public interface ApiInterface {


        //------------Login----------------------
        @POST("/spLoginUser.php")
        public void login(@Body Map<String, String> options, Callback<LoginResult> cb);

        //------------GetVisitSummary----------------------
        @POST("/spSummary.php")
        public void getVisitSummary(@Body Map<String, Object> options, Callback<List<VisitSummaryResult>> cb);

        //------------GetVisitEntries----------------------
        @POST("/spGetGymVisitDates.php")
        public void getVisitEntries(@Body Map<String, Object> options, Callback<List<VisitEntriesResult>> cb);

        //------------GetVisitEntries----------------------
        @POST("/spGetGymMessages.php")
        public void getMessageList(@Body Map<String, Object> options, Callback<List<MessageResult>> cb);

        //------------GetVisitEntries----------------------
        @POST("/spAddGymVisitFeedback.php")
        public void addGymVisitFeedback(@Body Map<String, Object> options, Callback<Integer> cb);

        //------------AddPersonGymVisit----------------------
        @POST("/spAddPersonGymVisit.php")
        public void addPersonGymVisit(@Body Map<String, Object> options, Callback<Integer> cb);

        //------------Register Push id----------------------
        @POST("/spRegisterPushID.php")
        public void registerPushId(@Body Map<String, Object> options, Callback<Response> cb);

        //----------------- Update personGymVisit ------------
        @POST("/spUpdatePersonGymVisit.php")
        public void upDatePersonGymVisit(@Body Map<String, Object> options, Callback<Integer> cb);

    }
}

