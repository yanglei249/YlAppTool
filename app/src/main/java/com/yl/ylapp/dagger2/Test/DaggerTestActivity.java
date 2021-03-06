package com.yl.ylapp.dagger2.Test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yl.ylapp.R;

import javax.inject.Inject;

public class DaggerTestActivity extends AppCompatActivity {
    @Inject
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger2);
        DaggerPersonComponent.builder().build().inject(this);
        System.out.println(person.getName());

    }
}
