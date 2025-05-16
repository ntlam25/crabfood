package com.example.crabfood.ui.address;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crabfood.R;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.search.ApiType;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchEngineSettings;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.SearchSuggestionsCallback;
import com.mapbox.search.result.SearchSuggestion;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private EditText etSearch;
    private SearchResultsView searchResultsView;
    private SearchEngine searchEngine;
    private List<SearchSuggestion> currentSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        etSearch = findViewById(R.id.searchAddress);
        searchResultsView = findViewById(R.id.searchResultAddress);
        mapView = findViewById(R.id.mapView);

        mapboxMap = mapView.getMapboxMap();
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS);


        searchEngine = SearchEngine.createSearchEngine(ApiType.SBS,
                new SearchEngineSettings(getString(R.string.mapbox_access_token)));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void performSearch(String query) {
        SearchOptions options = new SearchOptions.Builder()
                .limit(10)  // Số lượng kết quả trả về
                .build();
        searchEngine.search(query,options ,  new SearchSuggestionsCallback() {
            @Override
            public void onSuggestions(@NonNull List<SearchSuggestion> list, @NonNull ResponseInfo responseInfo) {
                currentSuggestions = list;
                List<String> names = new ArrayList<>();
                for (SearchSuggestion s : list) {
                    names.add(s.getName());
                }
            }

            @Override
            public void onError(Exception e) {
                // Xử lý lỗi
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}