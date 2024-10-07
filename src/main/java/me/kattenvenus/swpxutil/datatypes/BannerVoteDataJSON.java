package me.kattenvenus.swpxutil.datatypes;
import com.google.gson.annotations.SerializedName;

import me.kattenvenus.swpxutil.utilities.ManageJSON;

import java.util.ArrayList;

public class BannerVoteDataJSON {

    @SerializedName("bannerData")
    private ArrayList<BannerVoteData> bannerVoteData = new ArrayList<>();
    public ArrayList<BannerVoteData> getBannerVoteData() { return bannerVoteData; }
    public void setBannerVoteData(ArrayList<BannerVoteData> bannerVoteData) { this.bannerVoteData = bannerVoteData; }

}
