package server.response

import com.google.gson.annotations.SerializedName

data class PlayerInfo(@SerializedName("id")val id:String,
                      @SerializedName("email")val email:String,
                      @SerializedName("avatarId")val avatarId:Int,
                      @SerializedName("boardId")val boardId:Int,
                      @SerializedName("cardId")val cardId:Int,
                      @SerializedName("alias")val alias:String,
                      @SerializedName("money")val money:Int,
                      @SerializedName("unlockedAvatars")val unlockedAvatars:Array<Int>,
                      @SerializedName("unlockedBoards")val unlockedBoards:Array<Int>,
                      @SerializedName("unlockedCards")val unlockedCards:Array<Int>,
                      @SerializedName("giftClaimedToday")val giftClaimedToday:Boolean,
                      @SerializedName("gameId")val gameId:String,
                      @SerializedName("privateWins")val privateWins:Int,
                      @SerializedName("privateTotal")val privateTotal:Int,
                      @SerializedName("publicWins")val publicWins:Int,
                      @SerializedName("publicTotal")val publicTotal:Int)
