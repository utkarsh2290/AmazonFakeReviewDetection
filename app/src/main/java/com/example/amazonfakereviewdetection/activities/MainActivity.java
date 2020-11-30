package com.example.amazonfakereviewdetection.activities;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amazonfakereviewdetection.R;
import com.example.amazonfakereviewdetection.api.RetrofitClient;
import com.example.amazonfakereviewdetection.model.ReviewOutput;
import com.google.android.material.snackbar.Snackbar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private EditText etLink;
    private Button btnPost,btn2Post;
    private TextView tvText,tvResult,tvError;
    private ImageView imageViewEmoticon,imageViewError;
    private ProgressBar progressBar;
    private int backButtonCount=0;
    private NestedScrollView parentLayout;
    private CardView cardViewResult,cardViewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLink =findViewById(R.id.etLink);
        btnPost=findViewById(R.id.btnPost);
        tvText= findViewById(R.id.tvText);
        tvResult=findViewById(R.id.tvResult);
        imageViewEmoticon=findViewById(R.id.imageviewEmoticon);
        imageViewError=findViewById(R.id.imageviewError);
        tvError=findViewById(R.id.tvError);
        progressBar =findViewById(R.id.progressBar);
        parentLayout=findViewById(R.id.parentLayout);
        cardViewResult=findViewById(R.id.cardViewResult);
        cardViewError=findViewById(R.id.cardViewError);
        btn2Post=findViewById(R.id.btn2Post);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        btn2Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parentLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
                etLink.setText("");
                imageViewError.setImageResource(R.drawable.ic_undraw_online_shopping_re_k1sv);
                cardViewError.setVisibility(View.GONE);
                tvText.setVisibility(View.GONE);
                cardViewResult.setVisibility(View.GONE);
                imageViewEmoticon.setVisibility(View.GONE);
                imageViewError.setVisibility(View.VISIBLE);
                etLink.setVisibility(View.VISIBLE);
                btnPost.setVisibility(View.VISIBLE);
                btn2Post.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
                btn2Post.setVisibility(View.GONE);
            }
        });

        btnPost.setOnClickListener(v -> {

            String link= etLink.getText().toString().trim();
            if (link.matches("")) {
                Snackbar.make(findViewById(android.R.id.content),"Please paste link of the amazon product in the given field",Snackbar.LENGTH_SHORT).setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.purple_500)).setTextColor(Color.WHITE).show();
                return;
            }

            imageViewError.setVisibility(View.GONE);
            etLink.setVisibility(View.GONE);
            btnPost.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            Call<ReviewOutput> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .postLink(link);

            call.enqueue(new Callback<ReviewOutput>() {

                             @RequiresApi(api = Build.VERSION_CODES.M)
                             @Override
                             public void onResponse(Call<ReviewOutput> call, Response<ReviewOutput> response) {

                                 backButtonCount=0;
                                 etLink.setText(" ");


                                 ReviewOutput reviewOutput= response.body();
                                 Log.d("POST WORKING",response.message());

                                 double fakePercent =  reviewOutput.getPercentFakeReview();
                                 double fPercent= round(fakePercent);
                                 double avgConfidence = reviewOutput.getAverageConfidence();

                                 if((fPercent==0.0) &&(avgConfidence==0.0)){
                                     progressBar.setVisibility(View.GONE);
                                     cardViewError.setVisibility(View.VISIBLE);
                                     imageViewError.setVisibility(View.VISIBLE);
                                     imageViewError.setImageResource(R.drawable.ic_undraw_online_posts_h475);
                                     btn2Post.setVisibility(View.VISIBLE);

                                 }

                                 else if(fPercent>66.66){
                                     progressBar.setVisibility(View.GONE);
                                     tvText.setVisibility(View.VISIBLE);
                                     cardViewResult.setVisibility(View.VISIBLE);
                                     btn2Post.setVisibility(View.VISIBLE);

                                     String text=(fPercent + "%"+ " " + "reviews are fake. We will suggest you to be completely sure before buying this product.");
                                     imageViewEmoticon.setImageResource(R.drawable.ic_frame_3);
                                     imageViewEmoticon.setVisibility(View.VISIBLE);
                                     imageViewError.setVisibility(View.GONE);
                                     btn2Post.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.notatall));
                                     SpannableString ss = new SpannableString(text);
                                     StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                                     ss.setSpan(boldSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                     parentLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.notatall));
                                     tvResult.setText(ss);

                                 }

                                 else if(fPercent >33.33){
                                     progressBar.setVisibility(View.GONE);
                                     tvText.setVisibility(View.VISIBLE);
                                     cardViewResult.setVisibility(View.VISIBLE);
                                     btn2Post.setVisibility(View.VISIBLE);

                                     String text= (fPercent + "%"+ " " +"reviews are fake. We will suggest you to think twice before buying this product.");
                                     imageViewEmoticon.setImageResource(R.drawable.ic_frame_2);
                                     imageViewEmoticon.setVisibility(View.VISIBLE);
                                     imageViewError.setVisibility(View.GONE);
                                     parentLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.notcompletly));
                                     btn2Post.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.notcompletly));
                                     SpannableString ss = new SpannableString(text);
                                     StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                                     ss.setSpan(boldSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                     tvResult.setText(ss);
                                 }

                                 else{
                                     progressBar.setVisibility(View.GONE);
                                     tvText.setVisibility(View.VISIBLE);
                                     cardViewResult.setVisibility(View.VISIBLE);
                                     btn2Post.setVisibility(View.VISIBLE);
                                     String text=("Only"+ " "+ fPercent + "%"+ " " + "reviews are fake. We will suggest you to go for this product.");
                                     imageViewEmoticon.setImageResource(R.drawable.ic_frame_1);
                                     imageViewEmoticon.setVisibility(View.VISIBLE);
                                     imageViewError.setVisibility(View.GONE);
                                     btn2Post.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.completly));
                                     parentLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.completly));
                                     SpannableString ss = new SpannableString(text);
                                     StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                                     ss.setSpan(boldSpan, 5, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                     tvResult.setText(ss);
                                 }
                             }

                             @Override
                             public void onFailure(Call<ReviewOutput> call, Throwable t) {
                                 progressBar.setVisibility(View.GONE);
                                 imageViewError.setVisibility(View.VISIBLE);
                                 imageViewError.setImageResource(R.drawable.ic_undraw_online_posts_h475);
                                 cardViewError.setVisibility(View.VISIBLE);
                                 btn2Post.setVisibility(View.VISIBLE);
                                 Log.d("CANNOT POSTTTTT", t.getMessage());
                             }
                         }
            );
        });
    }

    private double round(Double fakePercent) {
        BigDecimal bd = BigDecimal.valueOf(fakePercent);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        backButtonCount++;
        if (backButtonCount == 1) {

            parentLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
            etLink.setText("");
            imageViewError.setImageResource(R.drawable.ic_undraw_online_shopping_re_k1sv);
            cardViewError.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            cardViewResult.setVisibility(View.GONE);
            imageViewEmoticon.setVisibility(View.GONE);
            imageViewError.setVisibility(View.VISIBLE);
            etLink.setVisibility(View.VISIBLE);
            btnPost.setVisibility(View.VISIBLE);
            btn2Post.setVisibility(View.GONE);
            btn2Post.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        }

        else if (backButtonCount ==2) {
                Snackbar.make(findViewById(android.R.id.content),"Press back again to exit.",Snackbar.LENGTH_SHORT).setBackgroundTint(getColor(R.color.purple_500)).setTextColor(Color.WHITE).show();

            backButtonCount++;
        }

        else{
            backButtonCount = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}