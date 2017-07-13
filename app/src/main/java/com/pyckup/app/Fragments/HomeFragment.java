package com.pyckup.app.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pyckup.app.Activities.EditProfile;
import com.pyckup.app.Helper.AppHelper;
import com.pyckup.app.Helper.VolleyMultipartRequest;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;
import com.pyckup.app.Activities.AddCard;
import com.pyckup.app.Activities.CustomGooglePlacesSearch;
import com.pyckup.app.Activities.HistoryActivity;
import com.pyckup.app.Activities.ShowProfile;
import com.pyckup.app.Helper.ConnectionHelper;
import com.pyckup.app.Helper.CustomDialog;
import com.pyckup.app.Helper.DirectionsJSONParser;
import com.pyckup.app.Helper.SharedHelper;
import com.pyckup.app.Helper.URLHelper;
import com.pyckup.app.Models.CardInfo;
import com.pyckup.app.Models.Driver;
import com.pyckup.app.Models.PlacePredictions;
import com.pyckup.app.R;
import com.pyckup.app.Utils.MapAnimator;
import com.pyckup.app.Utils.MapRipple;
import com.pyckup.app.Utils.MyBoldTextView;
import com.pyckup.app.Utils.MyButton;
import com.pyckup.app.Utils.MyTextView;
import com.pyckup.app.Utils.Utilities;
import com.pyckup.app.XuberApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pyckup.app.XuberApplication.trimMessage;


public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "HomeFragment";

    Activity activity;
    Context context;
    View rootView;
    HomeFragmentListener listener;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public interface HomeFragmentListener {
    }


    private static final int SELECT_PHOTO = 100;
    String isPaid = "", paymentMode = "";
    Utilities utils = new Utilities();
    int flowValue = 0;
    String strCurrentStatus = "";
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private final int ADD_CARD_CODE = 435;
    private static final int REQUEST_LOCATION = 1450;
    String feedBackRating;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    Handler handleCheckStatus;
    String strPickLocation = "", strTag = "", strPickType = "";
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;
    Driver driver;
    //        <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;

    //        <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    FrameLayout frmSource, frmDestination;
    MyBoldTextView source, destination;
    ImageView imgMenu,mapfocus,imgBack, shadowBack;
    View tripLine;
    LinearLayout errorLayout;
//       <!--1. Request to providers -->

    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    MyBoldTextView lblPaymentType, lblPaymentChange,booking_id;
    MyButton btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    LayoutInflater viewInflater;

//        <!--1. Driver Details-->

    LinearLayout lnrHidePopup, lnrSearchAnimation, lnrProviderPopup, lnrPriceBase, lnrPricemin, lnrPricekm;
    ;
    ImageView imgProviderPopup;
    MyBoldTextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    MyButton btnDonePopup;

//         <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate;
    MyButton btnRequestRideConfirm;
    MyButton imgSchedule;
    CheckBox chkWallet;
    MyBoldTextView lblEta;
    MyBoldTextView lblQuantity;
    MyBoldTextView lblWeight;
    MyBoldTextView lblType;
    MyBoldTextView lblApproxAmount,surgeDiscount,surgeTxt;
    View lineView;

    ImageView sendImage;

    Boolean isImageChanged = false;
    public static int deviceHeight;
    public static int deviceWidth;

    LinearLayout ScheduleLayout;
    MyBoldTextView scheduleDate;
    MyBoldTextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

    LocationRequest mLocationRequest;

    EditText imageS;

//         <!--3. Waiting For Providers ...-->

    RelativeLayout lnrWaitingForProviders;
    MyBoldTextView lblNoMatch;
    ImageView imgCenter;
    MyButton btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    RippleBackground rippleBackground;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

//         <!--4. Driver Accepted ...-->

    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    MyBoldTextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblSurgePrice;
    RatingBar ratingProvider;
    MyButton btnCall, btnCancelTrip;

//          <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    MyBoldTextView lblBasePrice, lblExtraPrice, lblDistancePrice, lblCommision, lblTaxPrice, lblTotalPrice, lblPaymentTypeInvoice,
            lblPaymentChangeInvoice;
    ImageView imgPaymentTypeInvoice;
    MyButton btnPayNow;

//          <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    MyBoldTextView lblProviderNameRate;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    Float pro_rating;

//            <!-- Static marker-->

    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    MyButton btnDone;
    CameraPosition cmPosition;


    String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "", quantity_val = "", weight_val = "", comment_val = "", image_val = "";

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;

    //MArkers
    Marker availableProviders;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;

    MapRipple mapRipple;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            notificationTxt = bundle.getString("Notification");
            Log.e("HomeFragment", "onCreate: Notification" + notificationTxt);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        this.viewInflater = inflater;

        customDialog = new CustomDialog(context);
        if (customDialog != null)
        customDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init(rootView);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    initMap();
                    MapsInitializer.initialize(getActivity());
                }
            }
        }, 500);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void init(View rootView) {

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        statusCheck();
        getCards();
        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    utils.print("Handler", "Called");
                    checkStatus();
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 3000);
            }
        }, 3000);


//        <!-- Map frame -->
        mapLayout = (LinearLayout) rootView.findViewById(R.id.mapLayout);
        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = (LinearLayout) rootView.findViewById(R.id.sourceAndDestinationLayout);
        frmSource = (FrameLayout) rootView.findViewById(R.id.frmSource);
        frmDestination = (FrameLayout) rootView.findViewById(R.id.frmDestination);
        source = (MyBoldTextView) rootView.findViewById(R.id.source);
        destination = (MyBoldTextView) rootView.findViewById(R.id.destination);
        imgMenu = (ImageView) rootView.findViewById(R.id.imgMenu);
        imgSos = (ImageView) rootView.findViewById(R.id.imgSos);
        imgShareRide = (ImageView) rootView.findViewById(R.id.imgShareRide);
        mapfocus = (ImageView) rootView.findViewById(R.id.mapfocus);
        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        shadowBack = (ImageView) rootView.findViewById(R.id.shadowBack);
        tripLine = (View) rootView.findViewById(R.id.trip_line);

//        <!-- Request to providers-->

        lnrRequestProviders = (LinearLayout) rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = (RecyclerView) rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = (ImageView) rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentChange);
        booking_id = (MyBoldTextView) rootView.findViewById(R.id.booking_id);
        btnRequestRides = (MyButton) rootView.findViewById(R.id.btnRequestRides);

//        <!--  Driver and service type Details-->

        lnrSearchAnimation = (LinearLayout) rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = (LinearLayout) rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = (LinearLayout) rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = (LinearLayout) rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = (LinearLayout) rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = (LinearLayout) rootView.findViewById(R.id.lnrHidePopup);

        imgProviderPopup = (ImageView) rootView.findViewById(R.id.imgProviderPopup);

        lblServiceName = (MyBoldTextView) rootView.findViewById(R.id.lblServiceName);
        lblCapacity = (MyBoldTextView) rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = (MyBoldTextView) rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = (MyBoldTextView) rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = (MyBoldTextView) rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = (MyBoldTextView) rootView.findViewById(R.id.lblBasePricePopup);
        lblProviderDesc = (MyBoldTextView) rootView.findViewById(R.id.lblProviderDesc);

        btnDonePopup = (MyButton) rootView.findViewById(R.id.btnDonePopup);


//         <!--2. Approximate Rate ...-->

        lnrApproximate = (LinearLayout) rootView.findViewById(R.id.lnrApproximate);
        imgSchedule = (MyButton) rootView.findViewById(R.id.imgSchedule);
        chkWallet = (CheckBox) rootView.findViewById(R.id.chkWallet);
        lblEta = (MyBoldTextView) rootView.findViewById(R.id.lblEta);
        lblQuantity = (MyBoldTextView) rootView.findViewById(R.id.lblQuantity);
        lblWeight = (MyBoldTextView) rootView.findViewById(R.id.lblWeight);
        lblType = (MyBoldTextView) rootView.findViewById(R.id.lblType);
        lblApproxAmount = (MyBoldTextView) rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = (MyBoldTextView) rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = (MyBoldTextView) rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = (MyButton) rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = (View) rootView.findViewById(R.id.lineView);

        //Schedule Layout
        ScheduleLayout = (LinearLayout) rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = (MyBoldTextView) rootView.findViewById(R.id.scheduleDate);
        scheduleTime = (MyBoldTextView) rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = (MyButton) rootView.findViewById(R.id.scheduleBtn);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = (RelativeLayout) rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = (MyBoldTextView) rootView.findViewById(R.id.lblNoMatch);
        //imgCenter = (ImageView) rootView.findViewById(R.id.imgCenter);
        btnCancelRide = (MyButton) rootView.findViewById(R.id.btnCancelRide);
        rippleBackground = (RippleBackground) rootView.findViewById(R.id.content);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = (LinearLayout) rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = (LinearLayout) rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = (LinearLayout) rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = (ImageView) rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = (ImageView) rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = (MyBoldTextView) rootView.findViewById(R.id.lblProvider);
        lblStatus = (MyBoldTextView) rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = (MyBoldTextView) rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = (MyBoldTextView) rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = (MyBoldTextView) rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = (RatingBar) rootView.findViewById(R.id.ratingProvider);
        btnCall = (MyButton) rootView.findViewById(R.id.btnCall);
        btnCancelTrip = (MyButton) rootView.findViewById(R.id.btnCancelTrip);


//           <!--5. Invoice Layout ...-->

        lnrInvoice = (LinearLayout) rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = (MyBoldTextView) rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = (MyBoldTextView) rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = (MyBoldTextView) rootView.findViewById(R.id.lblDistancePrice);
        //lblCommision = (MyBoldTextView) rootView.findViewById(R.id.lblCommision);
        lblTaxPrice = (MyBoldTextView) rootView.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = (MyBoldTextView) rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = (ImageView) rootView.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = (MyButton) rootView.findViewById(R.id.btnPayNow);

//          <!--6. Rate provider Layout ...-->

        lnrRateProvider = (LinearLayout) rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = (MyBoldTextView) rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = (ImageView) rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = (EditText) rootView.findViewById(R.id.txtComments);
        ratingProviderRate = (RatingBar) rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (MyButton) rootView.findViewById(R.id.btnSubmitReview);

//            <!--Static marker-->

        rtlStaticMarker = (RelativeLayout) rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = (ImageView) rootView.findViewById(R.id.imgDestination);
        btnDone = (MyButton) rootView.findViewById(R.id.btnDone);


        btnRequestRides.setOnClickListener(new OnClick());
        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        frmDestination.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());

        flowValue = 0;
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    utils.print("", "Back key pressed!");
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 0;
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    } else {
                        getActivity().finish();
                    }
                    layoutChanges();
                    return true;
                }
                return false;
            }
        });

    }

    @SuppressWarnings("MissingPermission")
    void initMap() {

        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }

    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraChangeListener(this);
        }

//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                if (value == 0) {
//                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    mMap.setPadding(0, 0, 0, 0);
//                    mMap.getUiSettings().setZoomControlsEnabled(false);
//                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
//                    mMap.getUiSettings().setMapToolbarEnabled(false);
//                    mMap.getUiSettings().setCompassEnabled(false);
//
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//                    currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
//                    source_lat = "" + latitude;
//                    source_lng = "" + longitude;
//                    source_address = currentAddress;
//                    current_address = currentAddress;
//                    source.setText(currentAddress);
//                    value++;
//                }
//
//                current_lat = "" + location.getLatitude();
//                current_lng = "" + location.getLongitude();
//            }
//        });

    }


    @Override
    public void onLocationChanged(Location location) {

        if (marker != null){
            marker.remove();
        }
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
            marker = mMap.addMarker(markerOptions);

            // mMap is GoogleMap object, latLng is the location on map from which ripple should start

       /* if (mapRipple == null){
            mapRipple = new MapRipple(mMap, new LatLng(location.getLatitude(),location.getLongitude()), context);
            mapRipple.startRippleMapAnimation();
        }else{
            mapRipple.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        // Start Animation again only if it is not running
        if (!mapRipple.isAnimationRunning()) {
            mapRipple.startRippleMapAnimation();
        }*/
            Log.e("MAP", "onLocationChanged: 1 " + location.getLatitude());
            Log.e("MAP", "onLocationChanged: 2 " + location.getLongitude());
            current_lat = "" + location.getLatitude();
            current_lng = "" + location.getLongitude();

            if (value == 0) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setPadding(0, 0, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                source_lat = "" + latitude;
                source_lng = "" + longitude;
                source_address = currentAddress;
                current_address = currentAddress;
                source.setText(currentAddress);
                getProvidersList("");
                value++;
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
            }
        }
    }

    class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", source.getText().toString());
                    intent.putExtra("d_address", destination.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDestination:
                    Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent2.putExtra("cursor", "destination");
                    intent2.putExtra("s_address", source.getText().toString());
                    intent2.putExtra("d_address", destination.getText().toString());
                    startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.lblPaymentChange:
                    if (cardInfoArrayList.size() > 0)
                        showChooser();
                    else
                        gotoAddCard();
                    break;
                case R.id.btnRequestRides:
                    scheduledDate = "";
                    scheduledTime = "";
                    if (!source.getText().toString().equalsIgnoreCase("") &&
                            !destination.getText().toString().equalsIgnoreCase("")) {
                        getQuantityAndWeight();
                        //getApproximateFare();
                    } else {
                        Toast.makeText(context, "Please enter both pickup and drop locations", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnRequestRideConfirm:
                    SharedHelper.putKey(context, "name","");
                    //sendRequest();
                    getImageAndComment();
                    break;
                case R.id.btnPayNow:
                    payNow();
                    break;
                case R.id.btnSubmitReview:
                    submitReviewCall();
                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:
                    lnrHidePopup.setVisibility(View.GONE);
                    flowValue = 1;
                    layoutChanges();
                    click = 1;
                    break;
                case R.id.btnCancelRide:
                      showCancelRideDialog();
                    break;
                case R.id.btnCancelTrip:
                    if (btnCancelTrip.getText().toString().equals(getString(R.string.cancel_trip)))
                    showCancelRideDialog();
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver",driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent3 = new Intent(activity, ShowProfile.class);
                    intent3.putExtra("driver",driver);
                    startActivity(intent3);
                    break;
                case R.id.btnCall:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intentCall);
                    }
                    break;
                case R.id.btnDone:
                    pick_first = true;
                    try {
                        utils.print("centerLat", cmPosition.target.latitude + "");
                        utils.print("centerLong", cmPosition.target.longitude + "");

                        Geocoder geocoder = null;
                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        String city = "", state = "", address = "";

                        try {
                            addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (strPickType.equalsIgnoreCase("source")) {
                            source_address = "" + address + "," + city + "," + state;
                            source_lat = "" + cmPosition.target.latitude;
                            source_lng = "" + cmPosition.target.longitude;
                            if (dest_lat.equalsIgnoreCase("")){
                                Toast.makeText(context,"Select destination",Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", source_address);
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            }
                        } else {
                            dest_lat = "" + cmPosition.target.latitude;
                            if (dest_lat.equalsIgnoreCase(source_lat)) {
                                Toast.makeText(context, "Both source and destination are same", Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", source.getText().toString());
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                dest_address = "" + address + "," + city + "," + state;
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
//                            setValuesForSourceAndDestination();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 0;
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    if (NAV_DRAWER == 0 ) {
                        if (drawer != null)
                        drawer.openDrawer(Gravity.LEFT);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                        drawer.closeDrawers();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")){
                        crtLat = Double.parseDouble(current_lat);
                        crtLng = Double.parseDouble(current_lng);

                        if (crtLat != null && crtLng != null){
                            LatLng loc = new LatLng(crtLat, crtLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 7;
                    layoutChanges();
                    break;
                case R.id.scheduleBtn:
                    SharedHelper.putKey(context, "name","");
                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            //sendRequest();
                            sendRequestVolley();
                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                //sendRequest();
                                sendRequestVolley();
                            } else {
                                Toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;
            }
        }
    }

    public void navigateToShareScreen(String shareUrl) {
        try{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context,"first_name")+ " "+ SharedHelper.getKey(context,"last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT,"TRANXIT-"+"Mr/Mrs."+name +" would like to share a ride with you at "+
                    shareUrl+current_lat+","+current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder .setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sosRequest() {

    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder .setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog,null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancalReason = reasonEtxt.getText().toString();
                cancelRequest();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    void layoutChanges() {
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                lnrRequestProviders.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }
            lnrRequestProviders.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            frmSource.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            imgMenu.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            shadowBack.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText("" + context.getString(R.string.sample_date));
            scheduleTime.setText("" + context.getString(R.string.sample_time));
            if (flowValue == 0) {
                if (imgMenu.getVisibility() == View.GONE) {
                    if (mMap != null) {
                        mMap.clear();
                        stopAnim();
                        setupMap();
                    }
                }
                frmDestination.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                destination.setText("");
                source.setText("" + current_address);
                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 1) {
                frmSource.setVisibility(View.VISIBLE);
                frmDestination.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.VISIBLE);
                lnrRequestProviders.startAnimation(slide_up);
                lnrRequestProviders.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }
            } else if (flowValue == 2) {
                imgBack.setVisibility(View.VISIBLE);
                chkWallet.setChecked(false);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {
                imgBack.setVisibility(View.VISIBLE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
            } else if (flowValue == 4) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
            } else if (flowValue == 5) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
            } else if (flowValue == 6) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
            } else if (flowValue == 7) {
                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
            } else if (flowValue == 9) {
                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (lstProviderMarkers.size() > 0) {
            lstProviderMarkers.get(0).setVisible(cameraPosition.zoom > 7);
        }
        if (strPickLocation.equalsIgnoreCase("yes")) {
            cmPosition = cameraPosition;
            if (pick_first) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cmPosition.target.latitude,
                        cmPosition.target.longitude), 16.0f));
                pick_first = false;
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String title = "";
        if (marker != null && marker.getTitle() != null) {
            title = marker.getTitle();
            if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                LatLng markerLocation = sourceMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                source_lat = markerLocation.latitude + "";
                source_lng = markerLocation.longitude + "";

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {
                LatLng markerLocation = destinationMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                dest_lat = "" + markerLocation.latitude;
                dest_lng = "" + markerLocation.longitude;

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "destination", "" + address + "," + city + "," + state);
                        dest_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                } /*else {
                    showPermissionReqDialog();
                }*/
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context,"sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionReqDialog() {
      AlertDialog.Builder builder =  new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder .setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setMessage("Tranxit needs the location permission, Please accept to use location functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                       // requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",activity.getPackageName(),HomeFragment.TAG);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder .setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {
            if (parserTask != null){
                parserTask = null;
            }
            if (resultCode == Activity.RESULT_OK) {
                if (marker != null){
                    marker.remove();
                }
                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 16.0f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            dest_address = placePredictions.strDestAddress;
                            SharedHelper.putKey(context, "current_status", "2");
                        }
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                            source_lat = "" + placePredictions.strSourceLatitude;
                            source_lng = "" + placePredictions.strSourceLongitude;
                            source_address = placePredictions.strSourceAddress;
                            if (!placePredictions.strSourceLatitude.equalsIgnoreCase("") && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                double longitude = Double.parseDouble(placePredictions.strSourceLongitude);

                                LatLng location = new LatLng(latitude, longitude);

                                mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));

                                marker = mMap.addMarker(markerOptions);
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }

                        }

                        if (dest_address.equalsIgnoreCase("")) {
                            flowValue = 1;
                            source.setText(source_address);
                            getServiceList();
                        } else {
                            flowValue = 1;
                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));
                            }
                            getServiceList();
                        }
                        layoutChanges();
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            Log.e("GPS Result Status", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status", "onActivityResult: " + data);
        }else {
            Log.e("GPS Result Status else", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status else", "onActivityResult: " + data);
        }

        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(context, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(context, uri));
                    //profile_Image.setImageBitmap(reRotateImg);
                    imageS.setText(AppHelper.getPath(context, uri));
                    sendImage.setImageBitmap(reRotateImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    void showProviderPopup(JSONObject jsonObject) {
        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);

        Glide.with(activity).load(jsonObject.optString("image")).placeholder(R.drawable.pickup_drop_icon).dontAnimate()
                .error(R.drawable.pickup_drop_icon).into(imgProviderPopup);

        lnrPriceBase.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);

        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            lblCalculationType.setText("Distance");
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }

        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            }else {
                lblCapacity.setText(jsonObject.optString("capacity") +" peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }

        lblServiceName.setText("" + jsonObject.optString("name"));
        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("fixed"));
        lblPriceKm.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("price"));
        lblPriceMin.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("minute"));
        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblProviderDesc.setVisibility(View.GONE);
        } else {
            lblProviderDesc.setVisibility(View.VISIBLE);
            lblProviderDesc.setText("" + jsonObject.optString("description"));
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            }else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
            lblEta.setText(SharedHelper.getKey(context, "eta_time"));
            lblQuantity.setText(SharedHelper.getKey(context, "quantity"));
            lblWeight.setText(SharedHelper.getKey(context, "weight"));
            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")){
                lblType.setText(SharedHelper.getKey(context, "name"));
            }  else {
                lblType.setText("" + "Sedan");
            }

            if ((customDialog != null)&& (customDialog.isShowing()))
            customDialog.dismiss();
        }
    }

    private void getCards() {
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId("CASH");
                                        cardInfo.setCardType("CASH");
                                        cardInfo.setLastFour("CASH");
                                        cardInfoArrayList.add(cardInfo);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });

    }

    public void getServiceList() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetServices", response.toString());
                if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                    SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                }
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                if (response.length() > 0) {
                    currentPostion = 0;
                    ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                    rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    rcvServiceTypes.setAdapter(serviceListAdapter);
                    getProvidersList(SharedHelper.getKey(context, "service_type"));
                } else {
                    utils.displayMessage(getView(), getString(R.string.no_service));
                }
                mMap.clear();
                setValuesForSourceAndDestination();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SERVICE_LIST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                            flowValue = 1;
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                            flowValue = 1;
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                        flowValue = 1;
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                    flowValue = 1;
                    layoutChanges();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void getQuantityAndWeight()
    {
        final AlertDialog.Builder qwDialog = new AlertDialog.Builder(getActivity());

        View qwView = viewInflater.inflate(R.layout.get_quantity_and_weight, null);


        qwDialog.setView(qwView);

        final AlertDialog dialog = qwDialog.create();
        dialog.show();

        final EditText quantity = (EditText) qwView.findViewById(R.id.quantity);
        final EditText weight = (EditText) qwView.findViewById(R.id.weight);

        Button btnQuantityandWeight = (Button) qwView.findViewById(R.id.btnQuantityandWeight);

        btnQuantityandWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( quantity.getText().toString().isEmpty() )
                {
                    quantity.setError("Please enter quantity.");
                }
                else if ( weight.getText().toString().isEmpty() )
                {
                    weight.setError("Please enter weight.");
                }
                else
                {
                    quantity_val = quantity.getText().toString();
                    weight_val = weight.getText().toString();

                    getApproximateFare();

                    dialog.dismiss();
                }
            }
        });
    }

    public void getApproximateFare() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();
        JSONObject object = new JSONObject();
        String constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + source_lat
                + "&s_longitude=" + source_lng
                + "&d_latitude=" + dest_lat
                + "&d_longitude=" + dest_lng
                + "&quantity=" + quantity_val
                + "&weight=" + weight_val
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                        utils.print("ApproximateResponse", response.toString());
                        SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                        SharedHelper.putKey(context, "distance", response.optString("distance"));
                        SharedHelper.putKey(context, "eta_time", response.optString("time"));
                        SharedHelper.putKey(context, "quantity", response.optString("quantity"));
                        SharedHelper.putKey(context, "weight", response.optString("weight"));
                        SharedHelper.putKey(context, "surge", response.optString("surge"));
                        SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                        setValuesForApproximateLayout();
                        double wallet_balance = response.optDouble("wallet_balance");
                        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                            lineView.setVisibility(View.VISIBLE);
                            chkWallet.setVisibility(View.VISIBLE);
                        } else {
                            lineView.setVisibility(View.GONE);
                            chkWallet.setVisibility(View.GONE);
                        }
                        flowValue = 2;
                        layoutChanges();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("APPROXIMATE_RATE");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.showAlert(context, context.getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.showAlert(context, context.getString(R.string.please_try_again));
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    void getProvidersList(String strTag) {
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        customDialog.show();
        String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + current_lat +
                "&longitude=" + current_lng +
                "&service=" + strTag;

        utils.print("Get all providers", "" + providers_request);
        utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));

        for (int i = 0; i < lstProviderMarkers.size(); i++) {
            lstProviderMarkers.get(i).remove();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetProvidersList", response.toString());
//                customDialog.dismiss();
//                mMap.clear();
//
//                setValuesForSourceAndDestination();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObj = response.getJSONObject(i);
                        if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                            Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                            Double proLng = Double.parseDouble(jsonObj.getString("longitude"));


                            Float rotation = 0.0f;

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(proLat, proLng))
                                    .rotation(rotation)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

//                        availableProviders = mMap.addMarker(markerOptions);
                            lstProviderMarkers.add(mMap.addMarker(markerOptions));

                            builder.include(new LatLng(proLat, proLng));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CameraUpdate cu = null;
                LatLngBounds bounds = builder.build();

//                cu = CameraUpdateFactory.newLatLngBounds(bounds, mapLayout.getWidth(), mapLayout.getWidth(), context.getResources()
//                        .getDimensionPixelSize(R.dimen._50sdp));
//                mMap.moveCamera(cu);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
//                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
//                                utils.showAlert(context, getString(R.string.something_went_wrong));

                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAST_TRIPS");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                utils.showAlert(context, json);
                            } else {
//                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        } else {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
//                        utils.showAlert(context, context.getString(R.string.something_went_wrong));

                    }

                } else {
//                    utils.showAlert(context, context.getString(R.string.no_drivers_found));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public void getImageAndComment()
    {
        final AlertDialog.Builder icDialog = new AlertDialog.Builder(getActivity());

        View icView = viewInflater.inflate(R.layout.get_image_and_comment, null);


        icDialog.setView(icView);

        final AlertDialog dialog = icDialog.create();
        dialog.show();

        Button btnBrowseImage = (Button) icView.findViewById(R.id.btnBrowseImage);
        imageS = (EditText) icView.findViewById(R.id.image);
        sendImage = (ImageView) icView.findViewById(R.id.sendImage);
        final EditText commentS = (EditText) icView.findViewById(R.id.comment);

        Button btnImageandComment = (Button) icView.findViewById(R.id.btnImageandComment);

        btnBrowseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission())
                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                else
                    goToImageIntent();
            }
        });

        btnImageandComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( imageS.getText().toString().isEmpty() )
                {
                    imageS.setError("Please browse image");
                }
                else if ( commentS.getText().toString().isEmpty() )
                {
                    commentS.setError("Please enter comment.");
                }
                else
                {
                    image_val = imageS.getText().toString();
                    comment_val = commentS.getText().toString();

                    //getApproximateFare();

                    //sendRequest();
                    sendRequestVolley();

                    dialog.dismiss();
                }
            }
        });
    }

    public void sendRequestVolley()
    {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    utils.print("SendRequestResponse", response.toString());
                    if ((customDialog != null)&& (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (jsonObject.optString("request_id", "").equals("")) {
                        utils.displayMessage(getView(), jsonObject.optString("message"));
                    } else {
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(context, "request_id", "" + jsonObject.optString("request_id"));
                        if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                            scheduleTrip = true;
                        else
                            scheduleTrip = false;
                        flowValue = 3;
                        layoutChanges();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.e(TAG, "" + error);
                displayMessage(getString(R.string.something_went_wrong));

            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("s_latitude", source_lat);
                params.put("s_longitude", source_lng);
                params.put("d_latitude", dest_lat);
                params.put("d_longitude", dest_lng);
                params.put("s_address", source_address);
                params.put("d_address", dest_address);
                params.put("quantity", quantity_val);
                params.put("weight", weight_val);
                //params.put("image", image_val);
                params.put("comments", comment_val);
                params.put("service_type", SharedHelper.getKey(context, "service_type"));
                params.put("distance", SharedHelper.getKey(context, "distance"));

                params.put("schedule_date", scheduledDate);
                params.put("schedule_time", scheduledTime);

                if (chkWallet.isChecked()) {
                    params.put("use_wallet", "1");
                } else {
                    params.put("use_wallet", "0");
                }
                if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                    params.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                } else {
                    params.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                    params.put("card_id", SharedHelper.getKey(context, "card_id"));
                }
                utils.print("SendRequestInput", "" + params.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                //params.put("image", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                params.put("image", new VolleyMultipartRequest.DataPart("requestImage.jpg", AppHelper.getFileDataFromDrawable(sendImage.getDrawable()), "image/jpeg"));
                return params;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    public void sendRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("s_address", source_address);
            object.put("d_address", dest_address);
            object.put("quantity", quantity_val);
            object.put("weight", weight_val);
            object.put("image", image_val);
            object.put("comments", comment_val);
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));

            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);

            Log.e("Schedule Request", "sendRequest: " + object);

            if (chkWallet.isChecked()) {
                object.put("use_wallet", 1);
            } else {
                object.put("use_wallet", 0);
            }
            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        XuberApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    utils.print("SendRequestResponse", response.toString());
                    if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                    if (response.optString("request_id", "").equals("")) {
                        utils.displayMessage(getView(), response.optString("message"));
                    } else {
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                        if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                            scheduleTrip = true;
                        else
                            scheduleTrip = false;
                        flowValue = 3;
                        layoutChanges();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("SendREquest", "onErrorResponse: " + errorObj.optString("message"));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(context, errorObj.optString("error"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        }
                    } catch (Exception e) {
                        utils.showAlert(context, context.getString(R.string.something_went_wrong));
                    }
                } else {
                    utils.showAlert(context, context.getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getActivity().getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason",cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                mapClear();
                SharedHelper.putKey(context, "request_id", "");
                flowValue = 0;
                PreviousStatus = "";
                layoutChanges();
                setupMap();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    flowValue = 4;
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                    layoutChanges();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    source.setText(source_address);
                } else {
                    source.setText(current_address);
                }
            } else {
                source.setText(current_address);
            }

            if (!dest_lat.equalsIgnoreCase("")) {
                destination.setText(dest_address);
            }


            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                String url = getDirectionsUrl(sourceLatLng, destLatLng);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }

        }
    }

    private void refreshAccessToken(final String tag) {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                    getServiceList();
                } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                    //getApproximateFare();
                    getQuantityAndWeight();
                } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                    sendRequest();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                    getProvidersList("");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(getActivity());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + source_lat + "," + source_lng;
        String str_dest = "destination=" + dest_lat + "," + dest_lng;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        utils.print("url", url.toString());
        return url;

    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(activity, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    private void showChooser() {

        String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            if (cardInfoArrayList.get(i).getLastFour().equals("CASH")) {
                cardsList[i] = "CASH";
            } else {
                cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
            }
        }

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builderSingle.setTitle(getString(R.string.choose_payment));
        builderSingle.setSingleChoiceItems(cardsList, 0, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.custom_tv);
        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card;
            if (cardInfoArrayList.get(j).getLastFour().equals("CASH")) {
                card = "CASH";
            } else {
                card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            }
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                utils.print("Items clicked===>", "" + selectedPosition);
                getCardDetailsForPayment(cardInfoArrayList.get(selectedPosition));
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getCardDetailsForPayment(cardInfoArrayList.get(which));
//                        dialog.dismiss();
//                    }
//                });
        builderSingle.show();
    }


    private void getCardDetailsForPayment(CardInfo cardInfo) {

        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText("CASH");
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    public void payNow() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                flowValue = 6;
                layoutChanges();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = "";
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    private void checkStatus() {
        try {

            utils.print("Handler", "Inside");
            if (isInternet) {

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("Response", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            utils.print("response", "not null");
                            try {
                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                //Driver Detail
                                if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                    driver = new Driver();
                                    driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                    driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                    driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                    driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                    driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                    driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                }
                                String status = requestStatusCheckObject.optString("status");
                                String wallet = requestStatusCheckObject.optString("use_wallet");
                                source_lat = requestStatusCheckObject.optString("s_latitude");
                                source_lng = requestStatusCheckObject.optString("s_longitude");
                                dest_lat = requestStatusCheckObject.optString("d_latitude");
                                dest_lng = requestStatusCheckObject.optString("d_longitude");

                                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                }


                                // surge price
                                if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                    lblSurgePrice.setVisibility(View.VISIBLE);
                                } else {
                                    lblSurgePrice.setVisibility(View.GONE);
                                }

                                utils.print("PreviousStatus", "" + PreviousStatus);

                                if (!PreviousStatus.equals(status)) {
                                    mMap.clear();
                                    PreviousStatus = status;
                                    flowValue = 8;
                                    layoutChanges();
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                    reCreateMap();
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {
                                        case "SEARCHING":
                                            show(lnrWaitingForProviders);
                                            //rippleBackground.startRippleAnimation();
                                            strTag = "search_completed";
                                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                            }
                                            break;
                                        case "CANCELLED":
                                            strTag = "";
                                            imgSos.setVisibility(View.GONE);
                                            break;
                                        case "ACCEPTED":
                                            strTag = "ride_accepted";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                show(lnrProviderAccepted);
                                                flowValue = 9;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "STARTED":
                                            strTag = "ride_started";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select)
                                                        .error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                flowValue = 4;
                                                layoutChanges();
                                                if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "current_status", "");
                                                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                    intent.putExtra("tag", "upcoming");
                                                    startActivity(intent);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "ARRIVED":
                                            once = true;
                                            strTag = "ride_arrived";
                                            utils.print("MyTest", "ARRIVED");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                lblStatus.setText(getString(R.string.arrived));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "PICKEDUP":
                                            once = true;
                                            strTag = "ride_picked";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                imgSos.setVisibility(View.VISIBLE);
                                                //imgShareRide.setVisibility(View.VISIBLE);
                                                lblStatus.setText(getString(R.string.picked_up));
                                                btnCancelTrip.setText(getString(R.string.share));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "DROPPED":
                                            once = true;
                                            strTag = "";
                                            imgSos.setVisibility(View.VISIBLE);
                                            //imgShareRide.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("distance"));
                                                    //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("total"));
                                                }
                                                if (requestStatusCheckObject.optString("booking_id")!= null &&
                                                        !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                    booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                }else {
                                                    booking_id.setVisibility(View.GONE);
                                                }
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money1);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && wallet.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CASH AND WALLET");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CARD");
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    else
                                                        Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "COMPLETED":
                                            strTag = "";
                                            try {
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("distance"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("total"));
                                                }
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                imgSos.setVisibility(View.GONE);
                                                //imgShareRide.setVisibility(View.GONE);
                                               // lblCommision.setText(payment.optString("commision"));
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    btnPayNow.setVisibility(View.GONE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money1);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CARD");
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    else
                                                        Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                    //imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    // lblPaymentTypeInvoice.setText("CARD");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                }

                                if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                        "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
                                    utils.print("Livenavigation", "" + status);
                                    utils.print("Destination Current Lat", "" + requestStatusCheckObject.getJSONObject("provider").optString("latitude"));
                                    utils.print("Destination Current Lng", "" + requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                    liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                            requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                        } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                            SharedHelper.putKey(context, "current_status", "");
                            if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                    && !scheduledTime.equalsIgnoreCase("")) {
                               /* Toast.makeText(context, getString(R.string.schdule_accept), Toast.LENGTH_SHORT).show();
                                if (scheduleTrip){
                                    Intent intent = new Intent(activity,HistoryActivity.class);
                                    intent.putExtra("tag","upcoming");
                                    startActivity(intent);
                                }*/
                            }else
                                Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                        } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                        } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());

                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        utils.print("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));

                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);

            } else {
                utils.displayMessage(getView(), getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }
            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            String url = getDirectionsUrl(sourceLatLng, destLatLng);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }


    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

//    public void liveNavigation(String status, String lat, String lng) {
//       utils.print("Livenavigation", "ProLat" + lat + " ProLng" + lng);
//        Double proLat = Double.parseDouble(lat);
//        Double proLng = Double.parseDouble(lng);
//        Location targetLocation = new Location("");//provider name is unecessary
//        targetLocation.setLatitude(proLat);//your coords of course
//        targetLocation.setLongitude(proLng);
//        if (once) {
//            once = false;
//            LatLng latLng = new LatLng(proLat, proLng);
//            // Showing the current location in Google Map
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            // Zoom in the Google Map
//            mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
//            LatLng providerPosition = new LatLng(proLat, proLng);
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(providerPosition);
//            providerMarker = mMap.addMarker(markerOptions);
//            providerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.provider_loaction_marker));
//        }
////            animateMarker(providerMarker, providerMarker.getPosition(), false);
//
//        if (status.equalsIgnoreCase("PICKEDUP")) {
//            utils.animateMarker(mMap, targetLocation, providerMarker);
//        } else {
//            providerMarker.setPosition(new LatLng(proLat, proLng));
//        }
//    }

    public void liveNavigation(String status, String lat, String lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

            if (providerMarker != null) {
                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
            }

            providerMarker = mMap.addMarker(markerOptions);
        }

    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.e("GPS Location", "onResult: " + result);
                Log.e("GPS Location", "onResult Status: " + result.getStatus());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        showDialogForGPSIntent();
                        break;
                }
            }
        });
//	        }

    }

    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtCommentsRate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                destination.setText("");
                mapClear();
                flowValue = 0;
                getProvidersList("");
                layoutChanges();
                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                utils.print("Entering dowload url", "entrng");
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {

            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                utils.print("Entering dwnload task", "download task");
            } catch (Exception e) {
                utils.print("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            utils.print("Resultmap", result);

            parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /*
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, PolylineOptions> {
        //ProgressDialog progressDialog = new ProgressDialog(activity);
        // Parsing the data in non-ui thread
        PolylineOptions lineOptions = null;
        boolean polyDrawn = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            points.clear();
        }

        @Override
        protected PolylineOptions doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                utils.print("routes", routes.toString());
                utils.print("routes size", routes.size() + "");
                ///////////////////////////////////
                if (routes != null) {
                    // Traversing through all the routes
                    for (int i = 0; i < routes.size(); i++) {
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);

                        // Fetching all the points in i-th route
                        utils.print("path size", path.size() + "");
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            if (!point.get("lat").equalsIgnoreCase("") && !point.get("lng").equalsIgnoreCase("")) {
                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                                utils.print("abcde", points.toString());

                                if (j == 0) {
                                    sourceLatLng = new LatLng(lat, lng);
                                }
                                if (j == path.size()) {
                                    destLatLng = new LatLng(lat, lng);
                                }
                            }
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.BLACK);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lineOptions;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(PolylineOptions lineOptions) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(sourceLatLng).title("Source").draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));

            sourceMarker = mMap.addMarker(markerOptions);
            sourceMarker.setDraggable(true);
            MarkerOptions markerOptions1 = new MarkerOptions()
                    .position(destLatLng).draggable(true).title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
            destinationMarker = mMap.addMarker(markerOptions1);
            destinationMarker.setDraggable(true);


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLngBounds bounds;
            builder.include(sourceMarker.getPosition());
            builder.include(destinationMarker.getPosition());
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 20);
            mMap.moveCamera(cu);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if (lineOptions == null) {
                Toast.makeText(getActivity(), getString(R.string.no_route), Toast.LENGTH_SHORT).show();
                if ((customDialog != null)&& (customDialog.isShowing())){
                    customDialog.dismiss();
                }
                flowValue = 0;
                layoutChanges();
                Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                intent2.putExtra("cursor", "destination");
                intent2.putExtra("s_address", source.getText().toString());
                startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
            } else {
//                    mMap.addPolyline(lineOptions);
                startAnim(points);
                if ((customDialog != null)&& (customDialog.isShowing())){
                    customDialog.dismiss();
                }
            }
        }
    }


    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }


        @Override
        public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.service_type_list_item, null);
            return new ServiceListAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ServiceListAdapter.MyViewHolder holder, final int position) {
            utils.print("Title: ", "" + jsonArray.optJSONObject(position).optString("name") + " Image: " + jsonArray.optJSONObject(position).optString("image") + " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.car_select).dontAnimate().error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button);
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.text_color_white));
            } else {
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.car_select).dontAnimate().error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.black_text_color));
            }


            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == currentPostion) {
                        try {
                            lnrHidePopup.setVisibility(View.VISIBLE);
                            showProviderPopup(jsonArray.getJSONObject(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPostion = Integer.parseInt(view.getTag().toString());
                    SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                    SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                    notifyDataSetChanged();
                    utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
                    utils.print("Service name", "" + SharedHelper.getKey(context, "name"));
                    getProvidersList("service");
                }
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView serviceTitle;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceTitle = (MyTextView) itemView.findViewById(R.id.serviceItem);
                serviceImg = (ImageView) itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = (LinearLayout) itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = (FrameLayout) itemView.findViewById(R.id.selector_background);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }


    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(mMap, routeList);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
        super.onDestroy();
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}