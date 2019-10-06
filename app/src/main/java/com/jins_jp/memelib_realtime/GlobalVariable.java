package com.jins_jp.memelib_realtime;

import android.app.Application;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class GlobalVariable extends Application {
    private ArrayList<Integer> u_list, d_list, l_list ,r_list;
    private Double roll, pitch;
    private Boolean isTraing,firststart=true;
    private Integer negtive_max,nagscore,cut_point;
    private MediaPlayer mplayer;

    public void setRoll(Double roll){
        this.roll = roll;
    }
    public void setPitch(Double pitch){
        this.pitch = pitch;
    }
    public void setU(ArrayList<Integer> u){this.u_list = u;}
    public void setD(ArrayList<Integer> d){
        this.d_list = d;
    }
    public void setL(ArrayList<Integer> l){
        this.l_list = l;
    }
    public void setR(ArrayList<Integer> r){
        this.r_list = r;
    }
    public void setIsTraing(Boolean b){ this.isTraing = b;}
    public void set_negtive_max(Integer n){this.negtive_max=n;};
    public void setNagscore(Integer n){this.nagscore=n;}
    public void setMplayer(MediaPlayer m){this.mplayer=m;}
    public void setFirststart(boolean b ){this.firststart=b;}
    public void setCut_point(Integer c){this.cut_point=c;}

    public Double getRoll(){ return roll; }
    public Double getPitch(){ return pitch; }
    public ArrayList<Integer> getUlist(){ return u_list; }
    public ArrayList<Integer> getDlist(){ return d_list; }
    public ArrayList<Integer> getLlist(){ return l_list; }
    public ArrayList<Integer> getRlist(){ return r_list; }
    public Boolean getisTraing(){ return isTraing; }
    public Integer getNegtive_max(){return negtive_max;}
    public Integer getNagscore(){return nagscore;}
    public MediaPlayer getmPlayer(){return  mplayer;}
    public  Boolean getFirststart(){return firststart;}
    public Integer getCut_point(){return cut_point;}
}
