package server.response

import com.google.gson.annotations.SerializedName

data class PlayerInfo(@SerializedName("id")val id:String,
                      @SerializedName("email")val email:String,
                      @SerializedName("avatarId")val avatarId:Int,
                      @SerializedName("alias")val alias:String,
                      @SerializedName("money")val money:Int,
                      @SerializedName("unlockable")val unlockable:ArrayList<Int>,
                      @SerializedName("giftClaimedToday")val giftClaimedToday:Boolean,
                      @SerializedName("gameId")val gameId:String,
                      @SerializedName("privateWins")val privateWins:Int,
                      @SerializedName("privateTotal")val privateTotal:Int,
                      @SerializedName("publicWins")val publicWins:Int,
                      @SerializedName("publicTotal")val publicTotal:Int)
