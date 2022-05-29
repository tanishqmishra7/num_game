package com.example.ai_mad_numgame;
/*
   App will show your last performance at the start of the activity. New Tournament will start from
   all performance set to -1 again. And your new performance will be visible, when you return back to game
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;  //make changes at appropriate places to include this dependency

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    int matchCounter=0,gamecounter=0;
    int []performance={-1,-1,-1,-1,-1};
    int []score={-1,-1,-1};
    String operators[]={"+","-","*","/"};
    Random random=new Random();
    int correctButton;
    TextView textView2;
    Button button1,button2,button3,button4;
    public void load(View view){
        Button buttonClicked=(Button)view;
        if(buttonClicked.getTag().toString().equals(correctButton+"")){
            score[matchCounter++]=1;
        }else{
            score[matchCounter++]=0;
        }
        newMatch();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        textView2=findViewById(R.id.textView2);
        newMatch();
        sharedPreferences=this.getSharedPreferences("com.example.aiapp_2022", Context.MODE_PRIVATE);
//        int [][]d=dataPrep();
    }
    public void newMatch() {
        correctButton=random.nextInt(4);
        double o1 = random.nextInt(10),o2=random.nextInt(10);
        int n=random.nextInt(4);
        double a1=0,a2=0,a3=0,ans=0;
        String a,b,c,d;
        while(o2==0 && n==3)
            o2=random.nextInt(10);
        if(n==0){
            ans=o1+o2;
        }else if(n==1){
            ans=o1-o2;
        }else if(n==2){
            ans=o1*o2;
        }else if(n==3 && o2!=0.0){
            ans=o1/o2;
            while(a1==ans || a1==a2 || a1==a3)
                a1= random.nextInt(20)+2*random.nextDouble()-1;
            while(a2==ans || a1==a2 || a2==a3)
                a2= random.nextInt(20)+2*random.nextDouble()-1;
            a3=a2;
            while(a3==ans || a3==a2 || a1==a3)
                a3= random.nextInt(20)+2*random.nextDouble()-1;
        }
        while(a1==ans || a1==a2 || a1==a3)
            a1= random.nextInt(20);
        while(a2==ans || a1==a2 || a2==a3)
            a2= random.nextInt(20);
        a3=a2;
        while(a3==ans || a3==a2 || a1==a3)
            a3= random.nextInt(20);
        String operator = operators[n];
        textView2.setText(String.format("%.0f", o1) + operator + String.format("%.0f", o2));
        if(n!=3) {
            a=String.format("%.0f", ans);
            b=String.format("%.0f", a1);
            c=String.format("%.0f", a2);
            d=String.format("%.0f", a3);
        }else {
            a=String.format("%.2f", ans);
            b=String.format("%.2f", a1);
            c=String.format("%.2f", a2);
            d=String.format("%.2f", a3);
        }
        if(correctButton==0){
            button1.setText(a);
            button2.setText(b);
            button3.setText(c);
            button4.setText(d);
        }else if(correctButton==1 && !(o2==0 && n==3)){
            button2.setText(a);
            button1.setText(b);
            button3.setText(c);
            button4.setText(d);
        }else if(correctButton==2 && !(o2==0 && n==3)){
            button3.setText(a);
            button2.setText(b);
            button1.setText(c);
            button4.setText(d);
        }else if(correctButton==3 && !(o2==0 && n==3)){
            button4.setText(a);
            button2.setText(b);
            button3.setText(c);
            button1.setText(d);
        }
        if(matchCounter==3){
            matchCounter=0;
            int sc=sumOfScore();
            performance[gamecounter++]=sc;
            Toast.makeText(this,""+"Score: "+sc, Toast.LENGTH_SHORT).show();
            if(gamecounter<5)
                new AlertDialog.Builder(this)
                        .setTitle("Break")
                        .setMessage("Wanna play more")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                newMatch();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                System.exit(0);
                            }
                        }).show();
            sharedPreferences.edit().putString("data",new Gson().toJson(performance)).apply();
        }

        if(gamecounter==5){
            gamecounter=0;
            matchCounter=0;
            sharedPreferences=this.getSharedPreferences("com.example.aiapp_2022", Context.MODE_PRIVATE);
            int[][]dataFrame=dataPrep();
            double slope=LR.getSlope(dataFrame);
            Toast.makeText(this, "" + "Slope is: "+slope, Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle("Performance")
                    .setMessage(getInterpretation(dataFrame,slope))
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newMatch();
                        }
                    }).show();
            for(int i=0;i<5;i++)
                performance[i]=-1;
            sharedPreferences.edit().putString("data",new Gson().toJson(performance)).apply();
        }

    }

    public int sumOfScore(){
        int sum=score[0]+score[1]+score[2];
        return sum;
    }

    public int[][] dataPrep() {
        int[] data = new Gson().fromJson((sharedPreferences.getString("data", null)), performance.getClass());
        Log.i("data", Arrays.toString(data));
        int dataFrame[][] = new int[5][2];
        if(data==null)
            return null;
        for (int i = 0; i < data.length; i++) {
            dataFrame[i][0] = i + 1;
            dataFrame[i][1] = data[i];
        }
        return dataFrame;
    }

    public String getInterpretation(int [][]dataFrame,double slope){
        if(slope==0)
            if(dataFrame[0][1]==3)
                return "Perfect Game";
            else if(dataFrame[0][1]==0)
                return "Poor Performance";
            else
                return "Try Harder";
        else if (slope>0)
            return "You are improving";
        else
            return "You need treatment";
    }
}